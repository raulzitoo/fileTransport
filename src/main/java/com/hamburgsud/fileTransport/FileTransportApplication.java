package com.hamburgsud.fileTransport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hamburgsud.fileTransport.service.FileTransferService;

@SpringBootApplication
public class FileTransportApplication implements CommandLineRunner{

	@Autowired
	FileTransferService service;
	
	public static void main(String[] args) {
		SpringApplication.run(FileTransportApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		service.transfer();
		
	}
}
