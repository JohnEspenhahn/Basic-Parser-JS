package com.hahn.basic.test;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.hahn.basic.KavaMain;
import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.lexer.basic.BasicLexerFactory;
import com.hahn.basic.target.js.JSCommandFactory;
import com.hahn.basic.target.js.JSNodeOutputBuilderFactory;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.exceptions.DuplicateDefinitionException;

public class KavaTest {
	
	private KavaMain kava;
	
	@Test
	public void testIdentifiers() {
		kava = new KavaMain(new JSCommandFactory(), new BasicLexerFactory(), EnumToken.class, EnumExpression.class, new JSNodeOutputBuilderFactory());
		
		assertOK("real r = 1, j = 2; puts(r + j);");
		assertCompileException("real j = 1;\nreal j = 2;", DuplicateDefinitionException.class, 2);
		
		assertOK("Array<real> arr1 = [ 1, 2, 3];");
		assertOK("real[] arr2 = [ 1, 2, 3 ];");		
		assertCompileException("real[] arr = [ 1, 2 ];\nString s = arr[0];", 2);
		
		assertOK("String s = \"Hello World\";");
		assertOK("String s = \"Hello World\"; real r = 1; String s2 = s + r;");
		assertCompileException("String s = \"Hello World\";\nreal r = 1;\nreal r2 = s + r;", 3);
	}
	
	private void assertOK(String code) {
		System.out.println("In:" + code);
		
		String res = "";
		try {
			res = kava.handleInput(code).toString();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Wasn't OK");
		}
		
		System.out.println("OK:" + res);
		System.out.println();
		
		return;
	}
	
	private void assertCompileException(String code, int row) {
		assertCompileException(code, CompileException.class, row);
	}
	
	private void assertCompileException(String code, Class<? extends CompileException> type) {
		assertCompileException(code, type, 1);
	}

	private void assertCompileException(String code, Class<? extends CompileException> type, int row) {
		try {
			kava.handleInput(code);
		} catch (CompileException e) {
			if (e.getClass() != type) {
				fail("Wrong exception thrown " + e);
			} else if (e.getRow() != row) {
				fail("Expected exception to throw at " + row + " but got " + e.getRow());
			}
			
			return;
		}
		
		fail("Exception of type " + type + " not thrown");
	}
}
