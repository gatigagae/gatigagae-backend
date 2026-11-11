package com.gatigagae.gatigagae_api.global.s3;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;

	@Value("${app.s3.bucket}")
	private String bucket;

	// 트랜잭션이 커밋되면 oldKey를, 롤백되면 newKey을 지운다
	public String replace(String dir, MultipartFile file, String oldKey) {
		String newKey = dir + "/" + UUID.randomUUID();
		upload(newKey, file);

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				String unusedKey = (status == STATUS_COMMITTED) ? oldKey : newKey;
				if (unusedKey != null) {
					delete(unusedKey);
				}
			}
		});
		return newKey;
	}

	public String getUrl(String key) {
		if (key == null) {
			return null;
		}
		return s3Client.utilities().getUrl(request -> request.bucket(bucket).key(key)).toString();
	}

	private void upload(String key, MultipartFile file) {
		try {
			s3Client.putObject(request -> request.bucket(bucket).key(key).contentType(file.getContentType()),
				RequestBody.fromBytes(file.getBytes()));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}

	private void delete(String key) {
		s3Client.deleteObject(request -> request.bucket(bucket).key(key));
	}
}