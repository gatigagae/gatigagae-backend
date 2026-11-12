(() => {
    'use strict';

    const API = '/api/v1/pets';
    const OTHER = 'OTHER';
    const PHOTO_TYPES = ['image/jpeg', 'image/png', 'image/webp'];
    const MAX_PHOTO_SIDE = 1280;
    const PREVIEW_DELAY = 300;
    const COAT_LABELS = {DOUBLE: '이중모', SINGLE: '단일모'};
    const MESSAGES = {
        noCode: {
            title: 'QR 코드를 스캔해 주세요',
            text: '차량 화면에 뜬 QR 코드를 휴대폰 카메라로 스캔해서 들어와 주세요.',
        },
        expired: {
            title: 'QR 코드를 다시 띄워 주세요',
            text: 'QR 코드가 만료됐거나 이미 사용됐어요.\n차량 화면에서 QR 코드를 새로 띄운 뒤 다시 스캔해 주세요.',
        },
        loadFailed: {
            title: '정보를 불러오지 못했어요',
            text: '네트워크 상태를 확인하고 다시 시도해 주세요.',
            retry: true,
        },
    };

    const $ = (id) => document.getElementById(id);

    const views = {
        loading: $('loading-view'),
        message: $('message-view'),
        form: $('profile-form'),
    };
    const fields = {
        name: $('name'),
        customBreedName: $('custom-breed-name'),
        age: $('age'),
        weight: $('weight'),
        note: $('note'),
    };
    const ui = {
        page: document.querySelector('.page'),
        title: $('form-title'),
        photoFrame: $('photo-frame'),
        photoImage: $('photo-image'),
        photoButton: $('photo-button'),
        photoInput: $('photo-input'),
        breedButton: $('breed-button'),
        breedValue: $('breed-value'),
        coatType: $('coat-type'),
        brachycephalic: $('brachycephalic'),
        temperatureTitle: $('temperature-title'),
        temperatureValue: $('temperature-value'),
        temperatureBasis: $('temperature-basis'),
        formError: $('form-error'),
        cancel: $('cancel-button'),
        save: $('save-button'),
        backdrop: $('breed-backdrop'),
        sheet: $('breed-sheet'),
        search: $('breed-search'),
        list: $('breed-list'),
        empty: $('breed-empty'),
        messageIcon: $('message-icon'),
        messageTitle: $('message-title'),
        messageText: $('message-text'),
        messageAction: $('message-action'),
    };
    // 오류를 표시할 칸
    const controls = {
        photo: ui.photoButton,
        name: fields.name,
        breed: ui.breedButton,
        customBreedName: fields.customBreedName,
        coatType: ui.coatType,
        brachycephalic: ui.brachycephalic,
        age: fields.age,
        weight: fields.weight,
        note: fields.note,
    };

    const state = {
        code: new URLSearchParams(location.search).get('code'),
        breeds: [],
        breed: null,
        coatType: null,
        brachycephalic: null,
        newPhoto: null,       // 새로 고른 사진 { blob, url }
        savedPhotoUrl: null,  // 저장돼 있는 사진 주소
        initial: null,        // 처음 불러온 값. 취소하면 이 값으로 되돌린다
        saving: false,
        sheetOpen: false,
    };

    let previewTimer = null;
    let previewController = null;
    let lastFocus = null;

    class ApiError extends Error {
        constructor(status, code, message, detail) {
            super(message);
            this.status = status;
            this.code = code;
            this.detail = detail;
        }
    }

    // 공통 응답 { success, code, message, result, error }에서 result를 꺼냄
    async function request(url, options = {}) {
        let response;
        try {
            response = await fetch(url, {...options, headers: {Accept: 'application/json'}});
        } catch {
            throw new ApiError(0, 'NETWORK', '네트워크 연결을 확인해 주세요.');
        }
        const body = await response.json().catch(() => null);
        if (response.ok && body?.success) {
            return body.result;
        }
        throw new ApiError(response.status, body?.code ?? `HTTP_${response.status}`,
            body?.message ?? '요청을 처리하지 못했어요.', body?.error);
    }

    const profileUrl = () => `${API}/profile?code=${encodeURIComponent(state.code)}`;


    async function init() {
        bindEvents();
        if (!state.code) {
            showMessage(MESSAGES.noCode);
            return;
        }

        let breeds;
        let pet;
        try {
            [breeds, pet] = await Promise.all([
                request(`${API}/breeds`),
                // 처음 등록하는 반려견이면 빈 칸으로 시작한다
                request(profileUrl()).catch((error) => {
                    if (error.code === 'PET_404_01') {
                        return null;
                    }
                    throw error;
                }),
            ]);
        } catch (error) {
            showMessage(error.code === 'PET_401_01' ? MESSAGES.expired : MESSAGES.loadFailed);
            return;
        }
        state.breeds = sortBreeds(breeds);
        startForm(pet);
    }

    function startForm(pet) {
        ui.title.textContent = pet ? '반려견 프로필 수정' : '반려견 프로필 등록';
        state.savedPhotoUrl = pet?.profileImageUrl ?? null;
        state.initial = {
            values: {
                name: pet?.name ?? '',
                breed: pet?.breed ?? null,
                customBreedName: pet?.customBreedName ?? '',
                coatType: pet?.coatType ?? null,
                brachycephalic: pet?.brachycephalic ?? null,
                age: pet ? String(pet.age) : '',
                weight: pet?.weight != null ? String(pet.weight) : '',
                note: pet?.note ?? '',
            },
            temperature: pet?.temperature ?? null,
        };
        applyInitial();
        showView('form');
    }

    function applyInitial() {
        const {values, temperature} = state.initial;
        fields.name.value = values.name;
        fields.customBreedName.value = values.customBreedName;
        fields.age.value = values.age;
        fields.weight.value = values.weight;
        fields.note.value = values.note;
        state.breed = values.breed;
        state.coatType = values.coatType;
        state.brachycephalic = values.brachycephalic;
        clearNewPhoto();
        showPhoto(state.savedPhotoUrl);
        clearAllErrors();
        renderChoices();
        cancelPreview();
        renderTemperatureTitle();
        renderTemperatureBasis();
        showTemperature(temperature);
        updateCancelState();
    }

    function currentValues() {
        return {
            name: fields.name.value,
            breed: state.breed,
            customBreedName: fields.customBreedName.value,
            coatType: state.coatType,
            brachycephalic: state.brachycephalic,
            age: fields.age.value,
            weight: fields.weight.value,
            note: fields.note.value,
        };
    }

    function isDirty() {
        return state.newPhoto !== null || JSON.stringify(currentValues()) !== JSON.stringify(state.initial.values);
    }

    function updateCancelState() {
        ui.cancel.disabled = state.saving || !isDirty();
    }

    function showView(name) {
        for (const [key, view] of Object.entries(views)) {
            view.hidden = key !== name;
        }
        window.scrollTo(0, 0);
    }

    function showMessage({title, text, retry = false, done = false}) {
        closeSheet();
        ui.messageIcon.textContent = done ? '✓' : '!';
        ui.messageIcon.classList.toggle('is-done', done);
        ui.messageTitle.textContent = title;
        ui.messageText.textContent = text;
        ui.messageAction.hidden = !retry;
        showView('message');
    }

    /* ---------- 이벤트 ---------- */

    function bindEvents() {
        ui.photoButton.addEventListener('click', () => ui.photoInput.click());
        ui.photoInput.addEventListener('change', onPhotoChange);
        ui.photoImage.addEventListener('error', () => showPhoto(null));

        fields.name.addEventListener('input', () => {
            setFieldError('name', '');
            renderTemperatureTitle();
            updateCancelState();
        });
        fields.customBreedName.addEventListener('input', () => {
            setFieldError('customBreedName', '');
            updateCancelState();
        });
        fields.age.addEventListener('input', () => {
            keepDigits(fields.age);
            setFieldError('age', '');
            onTemperatureInput(PREVIEW_DELAY);
        });
        fields.weight.addEventListener('input', () => {
            keepDecimal(fields.weight);
            setFieldError('weight', '');
            onTemperatureInput(PREVIEW_DELAY);
        });
        // 칸을 벗어날 때는 잘못 쓴 값만 알려주고, 빈 칸은 저장할 때 알려준다
        fields.age.addEventListener('blur', () => {
            if (ageText()) {
                setFieldError('age', ageMessage());
            }
        });
        fields.weight.addEventListener('blur', () => setFieldError('weight', weightMessage()));
        fields.note.addEventListener('input', () => {
            setFieldError('note', '');
            updateCancelState();
        });

        bindSegment(ui.coatType, 'coatType', (value) => value);
        bindSegment(ui.brachycephalic, 'brachycephalic', (value) => value === 'true');

        ui.breedButton.addEventListener('click', openSheet);
        ui.backdrop.addEventListener('click', closeSheet);
        ui.search.addEventListener('input', renderBreedList);
        ui.list.addEventListener('click', (event) => {
            const option = event.target.closest('.breed-option');
            if (option) {
                selectBreed(option.dataset.breed);
            }
        });
        document.addEventListener('keydown', (event) => {
            if (event.key === 'Escape') {
                closeSheet();
            } else if (event.key === 'Enter' && event.target instanceof HTMLInputElement) {
                // 휴대폰 키보드의 엔터를 눌러도 저장되지 않게 하고 키보드만 닫는다
                event.preventDefault();
                event.target.blur();
            }
        });

        ui.cancel.addEventListener('click', onCancel);
        views.form.addEventListener('submit', onSubmit);
        ui.messageAction.addEventListener('click', () => location.reload());
    }

    function bindSegment(container, name, toValue) {
        container.addEventListener('click', (event) => {
            const button = event.target.closest('button[data-value]');
            if (!button || state[name] === toValue(button.dataset.value)) {
                return;
            }
            state[name] = toValue(button.dataset.value);
            setFieldError(name, '');
            renderChoices();
            onTemperatureInput(0);
        });
    }

    function keepDigits(input) {
        const value = input.value.replace(/\D/g, '');
        if (value !== input.value) {
            input.value = value;
        }
    }

    // 쉼표는 소수점으로 바꾸고, 소수점은 하나만, 소수점 아래는 둘째 자리까지만 남긴다
    function keepDecimal(input) {
        const [whole, ...rest] = input.value.replace(/,/g, '.').replace(/[^\d.]/g, '').split('.');
        const value = rest.length ? `${whole}.${rest.join('').slice(0, 2)}` : whole;
        if (value !== input.value) {
            input.value = value;
        }
    }

    /* ---------- 견종 ---------- */

    // 가나다순으로 보여주고 '목록에 없어요'는 맨 아래에 둔다
    function sortBreeds(breeds) {
        return [...breeds].sort((a, b) => {
            if (a.breed === OTHER || b.breed === OTHER) {
                return a.breed === OTHER ? 1 : -1;
            }
            return a.name.localeCompare(b.name, 'ko');
        });
    }

    const findBreed = (code) => state.breeds.find((breed) => breed.breed === code);
    const breedLabel = (breed) => (breed.breed === OTHER ? '목록에 없어요' : breed.name);


    function selectBreed(code) {
        const breed = findBreed(code);
        if (!breed) {
            return;
        }
        if (state.breed !== code) {
            state.breed = code;
            // 견종으로 정해지는 값은 채우고, 정해지지 않으면 보호자가 직접 고르도록 비운다
            state.coatType = breed.coatType ?? null;
            state.brachycephalic = breed.brachycephalic ?? null;
            for (const name of ['breed', 'customBreedName', 'coatType', 'brachycephalic']) {
                setFieldError(name, '');
            }
            renderChoices();
            onTemperatureInput(0);
        }
        closeSheet();
    }

    function renderChoices() {
        const breed = findBreed(state.breed);
        ui.breedValue.textContent = breed ? breedLabel(breed) : '견종을 골라 주세요';
        ui.breedValue.classList.toggle('is-placeholder', !breed);
        fields.customBreedName.hidden = state.breed !== OTHER;
        renderSegment(ui.coatType, state.coatType);
        renderSegment(ui.brachycephalic, state.brachycephalic);
    }

    function renderSegment(container, value) {
        for (const button of container.querySelectorAll('button[data-value]')) {
            button.setAttribute('aria-pressed', String(button.dataset.value === String(value)));
        }
    }

    function openSheet() {
        if (state.sheetOpen) {
            return;
        }
        state.sheetOpen = true;
        lastFocus = document.activeElement;
        ui.search.value = '';
        renderBreedList();
        ui.backdrop.hidden = false;
        ui.sheet.hidden = false;
        ui.page.inert = true;
        document.body.classList.add('is-locked');
        void ui.sheet.offsetHeight;
        ui.backdrop.classList.add('is-open');
        ui.sheet.classList.add('is-open');
        ui.sheet.focus({preventScroll: true});

        // 고른 견종이 목록 가운데에 보이게 한다
        const current = ui.list.querySelector('[aria-current="true"]');
        if (current) {
            ui.list.scrollTop = current.offsetTop - ui.list.offsetTop - (ui.list.clientHeight - current.offsetHeight) / 2;
        }
    }

    function closeSheet() {
        if (!state.sheetOpen) {
            return;
        }
        state.sheetOpen = false;
        ui.backdrop.classList.remove('is-open');
        ui.sheet.classList.remove('is-open');
        ui.page.inert = false;
        document.body.classList.remove('is-locked');
        setTimeout(() => {
            if (!state.sheetOpen) {
                ui.backdrop.hidden = true;
                ui.sheet.hidden = true;
            }
        }, 250);
        lastFocus?.focus({preventScroll: true});
    }

    const normalize = (text) => text.toLowerCase().replace(/\s+/g, '');

    function renderBreedList() {
        const query = normalize(ui.search.value);
        const matched = state.breeds.filter((breed) => breed.breed !== OTHER
            && normalize(breed.name).includes(query));
        // '목록에 없어요'는 무엇을 찾든 맨 아래에 보여준다
        ui.list.replaceChildren(...[...matched, findBreed(OTHER)].map(createBreedOption));
        ui.empty.hidden = matched.length > 0;
    }

    function createBreedOption(breed) {
        const item = document.createElement('li');
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'breed-option';
        button.dataset.breed = breed.breed;
        if (breed.breed === state.breed) {
            button.setAttribute('aria-current', 'true');
        }

        const name = document.createElement('span');
        name.className = 'breed-name';
        name.textContent = breedLabel(breed);
        button.append(name);

        // '목록에 없어요' 옆에만
        if (breed.breed === OTHER) {
            const meta = document.createElement('span');
            meta.className = 'breed-meta';
            meta.textContent = breed.name;
            button.append(meta);
        }

        const check = document.createElement('span');
        check.className = 'breed-check';
        check.textContent = '✓';
        check.setAttribute('aria-hidden', 'true');
        button.append(check);

        item.append(button);
        return item;
    }

    /* ---------- 맞춤 실내 온도 ---------- */

    const ageText = () => fields.age.value.trim();
    const weightText = () => fields.weight.value.trim().replace(/\.$/, '');
    const isValidAge = (age) => /^\d{1,2}$/.test(age) && Number(age) <= 30;
    const isValidWeight = (weight) => /^\d{1,3}(\.\d{1,2})?$/.test(weight) && Number(weight) > 0;

    // 온도를 구하는 데 필요한 값을 다 골랐는지
    function hasTemperatureInputs() {
        return Boolean(state.breed && state.coatType) && state.brachycephalic !== null && isValidAge(ageText());
    }

    function onTemperatureInput(delay) {
        clearTimeout(previewTimer);
        previewTimer = setTimeout(updatePreview, delay);
        updateCancelState();
    }

    function cancelPreview() {
        clearTimeout(previewTimer);
        previewController?.abort();
        previewController = null;
    }

    // 값이 바뀔 때마다 서버에 온도를 물어봄
    async function updatePreview() {
        cancelPreview();
        renderTemperatureBasis();
        const weight = weightText();
        if (!hasTemperatureInputs() || (weight && !isValidWeight(weight))) {
            showTemperature(null);
            return;
        }

        const params = new URLSearchParams({
            breed: state.breed,
            coatType: state.coatType,
            brachycephalic: String(state.brachycephalic),
            age: ageText(),
        });
        if (weight) {
            params.set('weight', weight);
        }
        const controller = new AbortController();
        previewController = controller;
        try {
            const {temperature} = await request(`${API}/temperature?${params}`, {signal: controller.signal});
            if (previewController === controller) {
                showTemperature(temperature);
            }
        } catch {
            if (previewController === controller) {
                showTemperature(null);
                ui.temperatureBasis.textContent = '온도를 불러오지 못했어요. 잠시 후 다시 선택해 주세요.';
            }
        }
    }

    function showTemperature(value) {
        const empty = value == null;
        ui.temperatureValue.textContent = empty ? '—' : `${value}℃`;
        ui.temperatureValue.classList.toggle('is-empty', empty);
    }

    function renderTemperatureTitle() {
        const name = fields.name.value.trim();
        ui.temperatureTitle.textContent = name ? `${name} 맞춤 실내 온도` : '맞춤 실내 온도';
    }

    function renderTemperatureBasis() {
        if (!hasTemperatureInputs()) {
            ui.temperatureBasis.textContent = '견종, 털 유형, 단두종 여부, 나이를 입력하면 알려드려요.';
            return;
        }
        const weight = weightText();
        if (weight && !isValidWeight(weight)) {
            ui.temperatureBasis.textContent = '몸무게를 다시 확인해 주세요.';
            return;
        }
        const parts = [
            COAT_LABELS[state.coatType],
            state.brachycephalic ? '단두종' : '단두종 아님',
            `${Number(ageText())}살`,
        ];
        if (weight) {
            parts.push(`${Number(weight)}kg`);
        }
        ui.temperatureBasis.textContent = `${parts.join(' · ')} 기준`;
    }

    /* ---------- 사진 ---------- */

    async function onPhotoChange() {
        const file = ui.photoInput.files[0];
        ui.photoInput.value = '';  // 같은 사진을 다시 골라도 change 이벤트가 오게 비운다
        if (!file) {
            return;
        }
        setFieldError('photo', '');
        ui.photoButton.disabled = true;
        ui.save.disabled = true;
        try {
            setNewPhoto(await shrinkPhoto(file));
        } catch {
            setFieldError('photo', PHOTO_TYPES.includes(file.type)
                ? '사진을 읽지 못했어요. 다른 사진을 골라 주세요.'
                : 'jpg, png, webp 사진만 올릴 수 있어요.');
        } finally {
            ui.photoButton.disabled = false;
            ui.save.disabled = false;
            updateCancelState();
        }
    }

    // 긴 변을 1280px 이하로 줄여 JPEG로 다시 만든다. 용량이 줄고, 사진에 담긴 촬영 위치 같은 정보도 빠진다
    async function shrinkPhoto(file) {
        const image = await loadImage(file);
        const scale = Math.min(1, MAX_PHOTO_SIDE / Math.max(image.naturalWidth, image.naturalHeight));
        const canvas = document.createElement('canvas');
        canvas.width = Math.max(1, Math.round(image.naturalWidth * scale));
        canvas.height = Math.max(1, Math.round(image.naturalHeight * scale));
        const context = canvas.getContext('2d');
        context.fillStyle = '#ffffff';  // 투명한 PNG는 흰 배경으로 채운다
        context.fillRect(0, 0, canvas.width, canvas.height);
        context.drawImage(image, 0, 0, canvas.width, canvas.height);

        const blob = await new Promise((resolve) => canvas.toBlob(resolve, 'image/jpeg', 0.85));
        if (!blob) {
            throw new Error('사진을 만들지 못했어요.');
        }
        return blob;
    }

    function loadImage(file) {
        return new Promise((resolve, reject) => {
            const url = URL.createObjectURL(file);
            const image = new Image();
            image.onload = () => {
                URL.revokeObjectURL(url);
                resolve(image);
            };
            image.onerror = () => {
                URL.revokeObjectURL(url);
                reject(new Error('사진을 읽지 못했어요.'));
            };
            image.src = url;
        });
    }

    function setNewPhoto(blob) {
        clearNewPhoto();
        state.newPhoto = {blob, url: URL.createObjectURL(blob)};
        showPhoto(state.newPhoto.url);
    }

    function clearNewPhoto() {
        if (state.newPhoto) {
            URL.revokeObjectURL(state.newPhoto.url);
            state.newPhoto = null;
        }
    }

    function showPhoto(url) {
        ui.photoFrame.classList.toggle('has-photo', Boolean(url));
        ui.photoImage.hidden = !url;
        if (url) {
            ui.photoImage.src = url;
        } else {
            ui.photoImage.removeAttribute('src');
        }
    }

    /* ---------- 입력 확인과 오류 ---------- */

    function validate() {
        const errors = {};
        if (!fields.name.value.trim()) {
            errors.name = '이름을 입력해 주세요.';
        }
        if (!state.breed) {
            errors.breed = '견종을 골라 주세요.';
        }
        if (!state.coatType) {
            errors.coatType = '털 유형을 골라 주세요.';
        }
        if (state.brachycephalic === null) {
            errors.brachycephalic = '단두종 여부를 골라 주세요.';
        }
        const ageError = ageMessage();
        if (ageError) {
            errors.age = ageError;
        }
        const weightError = weightMessage();
        if (weightError) {
            errors.weight = weightError;
        }
        return errors;
    }

    function ageMessage() {
        const age = ageText();
        if (!age) {
            return '나이를 입력해 주세요.';
        }
        return isValidAge(age) ? '' : '나이는 0부터 30 사이로 입력해 주세요.';
    }

    function weightMessage() {
        const weight = weightText();
        if (!weight || isValidWeight(weight)) {
            return '';
        }
        return '몸무게는 0보다 크게, 999.99kg 이하로 입력해 주세요.';
    }

    function setFieldError(name, message) {
        $(`error-${name}`).textContent = message;
        const control = controls[name];
        control.classList.toggle('is-invalid', Boolean(message));
        if (message) {
            control.setAttribute('aria-invalid', 'true');
        } else {
            control.removeAttribute('aria-invalid');
        }
    }

    function clearAllErrors() {
        for (const name of Object.keys(controls)) {
            setFieldError(name, '');
        }
        ui.formError.textContent = '';
    }

    // 첫 번째 오류 칸으로 이동한다. 칸에 붙지 않은 오류만 있으면 아래 오류 문구를 보여준다.
    function revealFirstError() {
        const name = Object.keys(controls).find((key) => $(`error-${key}`).textContent);
        if (!name) {
            ui.formError.scrollIntoView({behavior: 'smooth', block: 'center'});
            return;
        }
        const control = controls[name];
        const target = control.classList.contains('segmented') ? control.querySelector('button') : control;
        target.focus({preventScroll: true});
        control.scrollIntoView({behavior: 'smooth', block: 'center'});
    }

    // 서버 메시지는 해당 칸 아래에 보여주고, 칸을 모르는 메시지는 돌려준다
    function applyServerErrors(messages) {
        const rest = [];
        for (const message of messages) {
            const match = /^\[(\w+)\]\s*(.*)$/.exec(message);
            const name = match && (match[1] === 'profileImage' ? 'photo' : match[1]);
            if (name && Object.hasOwn(controls, name)) {
                setFieldError(name, match[2]);
            } else {
                rest.push(message);
            }
        }
        return rest;
    }

    /* ---------- 취소와 저장 ---------- */

    function onCancel() {
        if (!isDirty() || !window.confirm('입력한 내용을 처음 상태로 되돌릴까요?')) {
            return;
        }
        applyInitial();
    }

    async function onSubmit(event) {
        event.preventDefault();
        if (state.saving) {
            return;
        }
        clearAllErrors();
        const errors = validate();
        if (Object.keys(errors).length) {
            for (const [name, message] of Object.entries(errors)) {
                setFieldError(name, message);
            }
            revealFirstError();
            return;
        }

        setSaving(true);
        let pet;
        try {
            pet = await request(profileUrl(), {method: 'POST', body: buildFormData()});
        } catch (error) {
            setSaving(false);
            handleSaveError(error);
            return;
        }
        showDone(pet);
    }


    function buildFormData() {
        const data = new FormData();
        data.append('name', fields.name.value.trim());
        data.append('breed', state.breed);
        const customBreedName = fields.customBreedName.value.trim();
        if (state.breed === OTHER && customBreedName) {
            data.append('customBreedName', customBreedName);
        }
        data.append('coatType', state.coatType);
        data.append('brachycephalic', String(state.brachycephalic));
        data.append('age', ageText());
        const weight = weightText();
        if (weight) {
            data.append('weight', weight);
        }
        const note = fields.note.value.trim();
        if (note) {
            data.append('note', note);
        }
        if (state.newPhoto) {
            data.append('profileImage', state.newPhoto.blob, 'profile.jpg');
        }
        return data;
    }

    function handleSaveError(error) {
        if (error.code === 'PET_401_01') {
            showMessage(MESSAGES.expired);
            return;
        }
        if (error.code === 'REQ_413_01' || error.status === 413) {
            setFieldError('photo', '사진 용량이 너무 커요. 다른 사진을 골라 주세요.');
        } else if (error.code === 'PET_400_01') {
            setFieldError('photo', 'jpg, png, webp 사진만 올릴 수 있어요.');
        } else if (error.code === 'REQ_400_02' && Array.isArray(error.detail)) {
            ui.formError.textContent = applyServerErrors(error.detail).join('\n');
        } else if (error.code === 'NETWORK') {
            ui.formError.textContent = '네트워크 연결을 확인하고 다시 저장해 주세요.';
        } else {
            ui.formError.textContent = '저장하지 못했어요. 잠시 후 다시 시도해 주세요.';
        }
        revealFirstError();
    }

    function setSaving(saving) {
        state.saving = saving;
        ui.save.disabled = saving;
        views.form.inert = saving;
        ui.save.textContent = saving ? '저장 중…' : '저장';
        updateCancelState();
    }

    function showDone(pet) {
        clearNewPhoto();
        showMessage({
            done: true,
            title: '저장 완료',
            text: '차량에서 등록한 정보를 확인해 주세요.',
        });
    }

    init();
})();