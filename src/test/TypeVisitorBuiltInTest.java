package test;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.After;
import org.junit.Test;

import main.TypeVisitor;

/**
 * JUnit 4 Tests for {@link TypeVisitor} class. Checks type declaration and
 * reference counts for Java built-in classes
 *
 * @author Sze Lok Irene Chan
 * @since 24 March, 2018
 *
 */
public class TypeVisitorBuiltInTest {

	private static TypeVisitor visitor ;

	@After
	public void after(){
		visitor.resetCounters();
	}
	
	/**
	 * Configures ASTParser and visitor for source file
	 *
	 * @param source
	 * @param type
	 * @param expectedDeclarationCount
	 * @param expectedReferenceCount
	 */
	private static void configureParser(String source, String type, int expectedDeclarationCount,
			int expectedReferenceCount) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		// these are needed for binding to be resolved due to SOURCE is a char[]
		String[] srcPath = { TestSuite.SOURCE_DIR };
		String[] classPath = { TestSuite.BIN_DIR };
		parser.setEnvironment(classPath, srcPath, null, true);
		// parser.setEnvironment(null, null, null, true);
		parser.setUnitName("BuiltInTest");

		// ensures nodes are being parsed properly
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		parser.setCompilerOptions(options);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		visitor = new TypeVisitor();
		cu.accept(visitor);

		int decl_count = 0;
		int ref_count = 0;
		try {
			decl_count = visitor.getDecCount().get(type);
			ref_count = visitor.getRefCount().get(type);
		} catch (Exception e) {
		}
		
		Map<String, Integer> refmap = visitor.getRefCount();
		Map<String, Integer> decmap = visitor.getDecCount();
		
		System.out.println("declaration count: " + decmap);
		System.out.println("reference count: " + refmap);
		System.out.println();

		assertEquals(expectedDeclarationCount, decl_count);
		assertEquals(expectedReferenceCount, ref_count);

}
	
	/**
	 * Testing a String, looking for no reference to String
	 */
	@Test
	public void testString_Dec_0_Ref_0() {
		configureParser("public class KFC {String someStr;} ", "String", 0, 0);
	}

	/**
	 * Testing an double, looking for reference to double
	 */
	@Test
	public void testPrimitiveDouble_Dec_0_Ref_1() {
		configureParser("public class KFC {double primInt;} ", "double", 0, 1);
	}

	/**
	 * Testing a wrapper class Integer, looking for reference to java.lang.Integer
	 */
	@Test
	public void testWrapperClassInteger_Dec_0_Ref_1() {
		configureParser("public class KFC {Integer someInt;} ", "java.lang.Integer", 0, 1);
	}

	/**
	 * Testing a String[] Array FIELD, looking for reference to java.lang.String
	 */
	@Test
	public void testFieldStringArrayWithoutAllocation_String_Dec_0_Ref_1() {
		configureParser("public class KFC {String[] newArray;} ", "java.lang.String", 0, 1);
	}

	/**
	 * Testing a String[] with Array FIELD, looking for reference to java.lang.String[]
	 */
	@Test
	public void testFieldStringArrayWithoutAllocation_StringA_Dec_0_Ref_1() {
		configureParser("public class KFC {String[] newArray;} ", "java.lang.String[]", 0, 1);
	}

	/**
	 * Testing a String[] with Array variable, looking for reference to java.lang.String[]
	 */
	@Test
	public void testVarStringArrayWithoutAllocation_String_Dec_0_Ref_1() {
		configureParser("public class KFC { public void Foo(){ String[] str; }} ", "java.lang.String", 0, 1);
	}

	/**
	 * Testing a String[] with Array variable, looking for reference to java.lang.String[]
	 */
	@Test
	public void testVarStringArrayWithoutAllocation_StringA_Dec_0_Ref_1() {
		configureParser("public class KFC { public void Foo(){ String[] str; }} ", "java.lang.String[]", 0, 1);
	}

	/**
	 * Testing a String[] with array variable, looking for reference to java.lang.String[]
	 * Allocating an empty set. 
	 */
	@Test
	public void testVarStringArrayWithAllocation_String_Dec_0_Ref_1() {
		configureParser("public class KFC { public void Foo(){ String[] str = {}; }} ", "java.lang.String", 0, 1);
	}

	/**
	 * Testing a String[] with array variable, looking for reference to java.lang.String[]
	 * Allocating an empty set. 
	 */
	@Test
	public void testVarStringArrayWithAllocation_StringA_Dec_0_Ref_1() {
		configureParser("public class KFC { public void Foo(){ String[] str = {}; }} ", "java.lang.String[]", 0, 1);
	}

	/**
	 * Testing a String[] with Array variable, looking for reference to java.lang.String.
	 * Create new instance
	 */
	@Test
	public void testVarStringArrayWithAllocation_String_Dec_0_Ref_2() {
		configureParser("public class KFC { public void Foo(){ String[] str = new String[5]; }} ", "java.lang.String", 0, 2);
	}

	/**
	 * Testing a String[] with Array variable, looking for reference to java.lang.String[]
	 * Create new instance
	 */
	@Test
	public void testVarStringArrayWithAllocation_StringA_Dec_0_Ref_2() {
		configureParser("public class KFC { public void Foo(){ String[] str = new String[5]; }} ", "java.lang.String[]", 0, 2);
	}

	/**
	 * Testing a String[] with Array variable, and methods, looking for reference to java.lang.String.
	 * Create new instance for the variable.
	 */
	@Test
	public void testVarMethStringArrayWithAlloc_String_Dec_0_Ref_3() {
		configureParser("public class KFC { public String[] Foo(){ String[] str = new String[5]; return str; }} ", "java.lang.String", 0, 3);
	}

	/**
	 * Testing a String[] with Array variable, and methods, looking for reference to java.lang.String[]
	 * Create new instance for the variable.
	 */
	@Test
	public void testVarMethStringArrayWithAlloc_StringA_Dec_0_Ref_3() {
		configureParser("public class KFC { public String[] Foo(){ String[] str = new String[5]; return str; }} ", "java.lang.String[]", 0, 3);
	}

	/**
	 * Testing a String[] with method return new, looking for reference to java.lang.String[]
	 */
	@Test
	public void testVarMethStringArrayWithReturn_StringA_Dec_0_Ref_3() {
		configureParser("public class KFC { public String[] Foo(){ String[] str; return new String[]; }} ", "java.lang.String[]", 0, 3);
	}

	/**
	 * Testing a String[] with method return new, looking for reference to java.lang.String
	 */
	@Test
	public void testVarMethStringArrayWithReturn_String_Dec_0_Ref_3() {
		configureParser("public class KFC { public String[] Foo(){ String[] str; return new String[]; }} ", "java.lang.String", 0, 3);
	}

	/**
	 * Testing a List with imports, looking for reference to java.util.List
	 * Parameters are considered references on their own. 
	 */
	@Test
	public void testTypeOfParameterizedTypes_Dec_0_Ref_2() {
		configureParser("import java.util.List; public class KFC { List<String> alist; } ", "java.util.List", 0, 2);
	}

	/**
	 * Testing the parameters inside a type, looking for reference to java.lang.String
	 * Parameters are considered references on their own. 
	 */
	@Test
	public void testParamOfParameterizedTypes_Dec_0_Ref_1() {
		configureParser("import java.util.ArrayList; public class KFC { ArrayList<String> alist; } ", "java.lang.String", 0, 1);
	}

	/**
	 * Check if initializing a variable of String within a for-loop counts as a
	 * reference
	 */
	@Test
	public void testForLoopInitialization_Dec_0_Ref_1() {
		configureParser("public class KFC { public void method() { for (String s;;){} }}", "java.lang.String", 0, 1);
	}
	
	/**
	 * Check that a reference to HashMap<String, Integer> defaults to
	 * java.util.HashMap with java.util.HashMap import
	 */
	@Test
	public void testJavaUtilHashMapImportedParameterizedAndDeclared_Dec_0_Ref_3() {
		configureParser("import java.util.HashMap; class KFC { HashMap<String, Integer> map = new HashMap<String, Integer>();}",
				"java.util.HashMap", 0, 3);
	}
}
