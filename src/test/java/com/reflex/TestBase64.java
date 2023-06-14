package com.reflex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.reflex.repository.TaskUnitTestRepository;
import com.reflex.model.TaskUnitTest;

@SpringBootTest
public class TestBase64 {
	
	@Autowired
	TaskUnitTestRepository taskUnitTestRepository;

	@Test
	public void testBase64() {
		
		String someCode = 
    			"package com.reflex.Roman_to_Integer_JUnit;\r\n"
    			+ "\r\n"
    			+ "public class Solution {\r\n"
    			+ "	\r\n"
    			+ "	 public int romanToInt(String s) {\r\n"
    			+ "         int ans = 0, num = 0;\r\n"
    			+ "        for (int i = s.length()-1; i >= 0; i--) {\r\n"
    			+ "            switch(s.charAt(i)) {\r\n"
    			+ "                case 'I': num = 1; break;\r\n"
    			+ "                case 'V': num = 5; break;\r\n"
    			+ "                case 'X': num = 10; break;\r\n"
    			+ "                case 'L': num = 50; break;\r\n"
    			+ "                case 'C': num = 100; break;\r\n"
    			+ "                case 'D': num = 500; break;\r\n"
    			+ "                case 'M': num = 1000; break;\r\n"
    			+ "            }\r\n"
    			+ "            if (4 * num < ans) ans -= num;\r\n"
    			+ "            else ans += num;\r\n"
    			+ "        }\r\n"
    			+ "        return ans;\r\n"
    			+ "    }\r\n"
    			+ "\r\n"
    			+ "}";
		String someCode2 = 
    			"$$ package com.reflex.Roman_to_Integer_JUnit;\r\n"
    			+ "\r\n"
    			+ "public class Solution {\r\n"
    			+ "	\r\n"
    			+ "	 public int romanToInt(String s) {\r\n"
    			+ "         int ans = 0, num = 0;\r\n"
    			+ "        for (int i = s.length()-1; i >= 0; i--) {\r\n"
    			+ "            switch(s.charAt(i)) {\r\n"
    			+ "                case 'I': num = 1; break;\r\n"
    			+ "                case 'V': num = 5; break;\r\n"
    			+ "                case 'X': num = 10; break;\r\n"
    			+ "                case 'L': num = 50; break;\r\n"
    			+ "                case 'C': num = 100; break;\r\n"
    			+ "                case 'D': num = 500; break;\r\n"
    			+ "                case 'M': num = 1000; break;\r\n"
    			+ "            }\r\n"
    			+ "            if (4 * num < ans) ans -= num;\r\n"
    			+ "            else ans += num;\r\n"
    			+ "        }\r\n"
    			+ "        return ans;\r\n"
    			+ "    }\r\n"
    			+ "\r\n"
    			+ "} $$";
		
		TaskUnitTest unitTest = new TaskUnitTest();
		unitTest.setCode(someCode2);
		taskUnitTestRepository.save(unitTest);
		
	}
}
