package com.shirosso.appone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppOneApplicationTests {

	@Test
	public void contextLoads() {

	}

	public static void main(String[] args){
		System.out.println(UUID.randomUUID());
	}

}

