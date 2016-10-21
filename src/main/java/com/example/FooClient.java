package com.example;

import java.util.List;

import feign.RequestLine;

public interface FooClient {
	@RequestLine("GET /foos")
    List<Foo> getFoos();
}
