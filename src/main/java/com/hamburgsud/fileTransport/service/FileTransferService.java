package com.hamburgsud.fileTransport.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
public class FileTransferService {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${app.incomingFolder}")
	private String incomingfolder;
	@Value("${app.ftp.host}")
	private String ftpHost;
	@Value("${app.ftp.user}")
	private String ftpUser;
	@Value("${app.ftp.password}")
	private String ftpPassword;
	@Value("${app.send.email-onerror}")
	private boolean sendEmailOnError;
	@Value("${app.email}")
	private String errorEmail;
	@Value("${app.needBackup}")
	private boolean needBackup;
	@Value("${app.backupFolder}")
	private String backupFolder;
	@Value("${app.folderFtp}")
	private String folderFtp;

	public void transfer() {

		FTPClient client = new FTPClient();

		try {
			client.connect(ftpHost);
			client.login(ftpUser, ftpPassword);
			System.out.println("Passei login");
			if (!folderFtp.isEmpty())
				client.changeWorkingDirectory(folderFtp);
			
			File incoming = new File(incomingfolder);
			logger.info("Acessando pasta" + incomingfolder);
			if (!incoming.isDirectory())
				throw new Exception("não é diretório" );
			
			File[] listFiles = incoming.listFiles();
			for (File file : listFiles) {
				System.out.println(file.getName());
				try (FileInputStream stream = new FileInputStream(file)){
					if (client.storeFile(file.getName(), stream)) {
						stream.close();
						if (needBackup) {
							File backupfile = Paths.get(backupFolder + file.getName()).toFile();
							FileCopyUtils.copy(file, backupfile);
						}
						Files.delete(Paths.get(file.getPath()));
					}else
						throw new IOException("Não foi possivel transmitir o arquivo para o FTP.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("deu erro", e);
				}

			}

			client.logout();
		} catch (Exception e) {
			logger.error("Ocorreu um erro com a conexão com o FTP.", e);
		} finally {
			try {
				client.disconnect();
			} catch (IOException e) {
				logger.error("Ocorreu um erro ao desconectar do FTP.", e);
			}
		}
	}

}
