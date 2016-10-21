/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import feign.Client;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;

@SpringBootApplication
@EnableDiscoveryClient
public class ManualFeignClientApplication {

	@RestController
	@Import(FeignClientsConfiguration.class)  //This is the config class from Spring Cloud
	protected class FooController {

		private FooClient fooClient;

		private FooClient adminClient;

		@Autowired
		public FooController(
				ResponseEntityDecoder decoder, SpringEncoder encoder, Client client) {
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

	public static void main(String[] args) {
		SpringApplication.run(ManualFeignClientApplication.class, args);
	}
}
