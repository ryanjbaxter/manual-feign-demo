package com.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import feign.Client;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;

@RestController
public class MyController {
	private FooClient fooClient;

	private FooClient adminClient;

	@Autowired
	public MyController(
			Decoder decoder, Encoder encoder, Client client) {
		this.fooClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.requestInterceptor(new BasicAuthRequestInterceptor("user", "user"))
				.target(FooClient.class, "http://PROD-SVC");  //PROD-SVC is the name of the service registered in the discovery server
		this.adminClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.requestInterceptor(new BasicAuthRequestInterceptor("admin", "admin"))
				.target(FooClient.class, "http://PROD-SVC");
	}

	@RequestMapping("/user-foos")
	public ResponseEntity<List<Foo>> getUserFoos() {
		List<Foo> foos = fooClient.getFoos();
		return ResponseEntity.ok(foos);
	}

	@RequestMapping("/admin-foos")
	public ResponseEntity<List<Foo>> getAdminFoos() {
		List<Foo> foos = adminClient.getFoos();
		return ResponseEntity.ok(foos);
	}
}
