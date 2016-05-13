import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.rmi.ssl.SslRMIClientSocketFactory;

public class Genuml {

	public static String s = "@startuml\nskinparam classAttributeIconSize 0 \n";
	public static String current_class;
	public static ArrayList<String> parser_classes = new ArrayList<String>();
	public static ArrayList<String> association_variables = new ArrayList<String>();
	public static ArrayList<String> uses_class_interface_associations = new ArrayList<String>();
	public static ArrayList<String> class_interface_associations = new ArrayList<String>();
	public static ArrayList<String> get_set_variables = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		// creates an input stream for the file to be parsed

		File input_directory = new File(args[0]);
		File[] input_javafiles = input_directory.listFiles();
		if (input_javafiles != null) {

			/* array of class names */

			for (File classname : input_javafiles) {

				ArrayList<String> tokenisation = new ArrayList<String>();
				if ((classname.getName().contains(".java"))) {

					File file = new File(classname.getPath());

					FileInputStream in = new FileInputStream(file);
					CompilationUnit cu;
					try {

						cu = JavaParser.parse(in, "UTF8");
					}

					finally {
						in.close();
					}
					String temp = cu.toString();

					ArrayList<String> inputFile_line = new ArrayList<String>();

					String lines[] = temp.split("\\r?\\n");

					for (int i = 0; i < lines.length; i++) {
						inputFile_line.add(lines[i]);
						for (String l : inputFile_line) {
							String delimitor = "[ .,?!]+";
							String key[] = l.split(delimitor);
							for (int j = 0; j < key.length; j++) {
								tokenisation.add(key[j]);
							}
						}
					}
					// int i=0;
					for (int k = 0; k < tokenisation.size(); k++) {
						if (tokenisation.get(k).contains("class")) {
							current_class = tokenisation.get(k + 1);
							parser_classes.add(current_class);
							// uses_class_interface_associations.add(current_class);
						}
						if (tokenisation.get(k).contains("interface")) {
							current_class = tokenisation.get(k + 1);
							uses_class_interface_associations.add(current_class);
							parser_classes.add(current_class);
						}

					}

				}
			}
			/* array of class names end */

			for (File classname : input_javafiles) {

				if ((classname.getName().contains(".java"))) {

					File file = new File(classname.getPath());

					FileInputStream in = new FileInputStream(file);
					CompilationUnit cu;
					try {

						cu = JavaParser.parse(in, "UTF8");
					} finally {
						in.close();
					}
					String temp = cu.toString();
					String lines[] = temp.split("\\r?\\n");
					String delimitor = "[ .,?!]+";
					String[] tokenisation = lines[0].split(delimitor);

					List types = cu.getTypes();

					List types1 = cu.getTypes();

					TypeDeclaration typeDec = (TypeDeclaration) types.get(0);

					current_class = typeDec.getName();

					if (tokenisation[1].equals("interface"))
						s = s + "interface" + " " + current_class + "\n";
					if (tokenisation[1].equals("class"))
						s = s + "class" + " " + current_class + "\n";
					// visit and print the methods names

					new Parser_GetSetVariables().visit(cu, null);
					new Parser_FieldDeclaration().visit(cu, null);
					new parser_ConstructionDeclaration().visit(cu, null);
					new Parser_MethodDeclaration().visit(cu, null);
					new parser_ConstructorUsesNotation().visit(cu, null);
					new parser_extendsFunctionality().visit(cu, null);
					new parser_implementsFunctionality().visit(cu, null);
					new parser_AssociationNotation().visit(cu, null);
				}
			}
			s = s + "@enduml\n";
			System.out.println(s);
			// String destination=args[1];
			String destination = args[1];
			UMLgenerate p = new UMLgenerate();
			p.umlCreator(s, destination);
			System.out.println("Diagram successfully generated\n");

		}

	}

	private static class Parser_GetSetVariables extends VoidVisitorAdapter {
		public void visit(MethodDeclaration n, Object arg) {

			if (n.getName().contains("get") && (n.getModifiers() == 1))

			{

				String getter_method = n.getName().toLowerCase();
				String get_var = getter_method.substring(3, getter_method.length());
				get_set_variables.add(get_var);

			}

			else if (n.getName().contains("set") && (n.getModifiers() == 1)) {
				String setter_method = n.getName().toLowerCase();
				String set_var = setter_method.substring(3, setter_method.length());
				get_set_variables.add(set_var);
			}
		}
	}

	private static class Parser_FieldDeclaration extends VoidVisitorAdapter {

		@Override
		public void visit(FieldDeclaration n, Object arg) {

			int flag = 0;
			String k = n.toString();
			k = k.replaceAll("[;]", "");
			String[] modifiers = k.split("\\s+");
			if (modifiers[0].equals("public")) {
				modifiers[0] = "+";
				flag = 1;
			}
			if (modifiers[0].equals("private")) {
				modifiers[0] = "-";
				flag = 1;

			}

			if (flag > 0) {
				if ((get_set_variables.contains(modifiers[2])) && (modifiers[0].equals("-"))) {
					modifiers[0] = "+";
				}
				s = s + current_class + " : " + modifiers[0] + " " + modifiers[1] + " " + modifiers[2];
				s = s + "\n";
			}
			super.visit(n, arg);
		}

	}

	private static class parser_ConstructionDeclaration extends VoidVisitorAdapter {

		@Override
		public void visit(ConstructorDeclaration n, Object arg) {

			String mod = null;
			if (n.getModifiers() == 1) {
				mod = "+";
			} else if (n.getModifiers() == 2) {
				mod = "-";
			} else if (n.getModifiers() == 4) {
				mod = "#";
			} else {
				mod = "+";
			}
			s = s + current_class + " : " + mod + n.getName() + "()";

			s = s + "\n";

		}
	}

	private static class Parser_MethodDeclaration extends VoidVisitorAdapter {

		@Override
		public void visit(MethodDeclaration n, Object arg) {

			if (!((n.getName().contains("get")) || (n.getName().contains("set")))) {
				String mod = null;
				if (n.getModifiers() == 1) {
					mod = "+";
				} else if (n.getModifiers() == 2) {
					mod = "-";
				} else if (n.getModifiers() == 4) {
					mod = "#";
				} else {
					mod = "+";
				}
				s = s + current_class + " : " + mod + n.getName() + "():" + n.getType();

				s = s + "\n";
			}

			/* New code for uses START */
			ArrayList<String> tokenisation = new ArrayList<String>();
			if (n.getBody() != null) {
				String temp = n.getBody().toString();
				ArrayList<String> inputFile_line = new ArrayList<String>();

				String lines[] = temp.split("\\r?\\n");

				for (int i = 0; i < lines.length; i++) {
					inputFile_line.add(lines[i]);
				}
				for (String l : inputFile_line) {
					String delimitor = "[ .,?!]+";
					String key[] = l.split(delimitor);
					for (int j = 0; j < key.length; j++) {
						tokenisation.add(key[j]);
					}
				}
				for (int k = 0; k < tokenisation.size(); k++) {
					if (uses_class_interface_associations.contains(tokenisation.get(k))) {
						s = s + current_class + " ..> " + tokenisation.get(k) + "\n";
					}
				}

			}
			/* New Code for uses END */

			String[] coll;

			if (n.getParameters() != null) {
				coll = n.getParameters().toString().split("[\\[\\s]");
				if (!(association_variables.contains(coll[1]))) {
					association_variables.add(coll[1]);
					if ((uses_class_interface_associations).contains(coll[1])) {

						s = s + current_class + " ..> " + coll[1] + "\n";
					}
				}

			}
		}

	}

	private static class parser_ConstructorUsesNotation extends VoidVisitorAdapter<Object> {

		@Override
		public void visit(ConstructorDeclaration n, Object arg) {
			String[] constructor_param;
			if (n.getParameters() != null) {
				constructor_param = n.getParameters().toString().split("[\\[\\s]");

				if ((uses_class_interface_associations).contains(constructor_param[1])) {

					s = s + current_class + " ..> " + constructor_param[1] + "\n";
				}
			}
		}

	}

	private static class parser_extendsFunctionality extends VoidVisitorAdapter {

		@Override
		public void visit(ClassOrInterfaceDeclaration decl, Object arg) {
			// Make class extend//

			List<ClassOrInterfaceType> list = decl.getExtends();
			if (list == null)
				return;
			for (ClassOrInterfaceType k : list) {
				String class_name = k.toString();
				s = s + class_name + " " + "<|--" + " " + current_class + "\n";
			}

		}

	}

	private static class parser_implementsFunctionality extends VoidVisitorAdapter {

		@Override
		public void visit(ClassOrInterfaceDeclaration decl, Object arg) {
			// Make class extend Blah.

			List<ClassOrInterfaceType> list = decl.getImplements();
			if (list == null)
				return;
			for (ClassOrInterfaceType k : list) {
				String interface_name = k.toString();
				s = s + interface_name + " " + "<|.." + " " + current_class + "\n";
			}

		}
	}

	private static class parser_AssociationNotation extends VoidVisitorAdapter {

		@Override
		public void visit(FieldDeclaration n, Object arg) {

			if ((parser_classes).contains(n.getType().toString())) {
				String relation = current_class + n.getType().toString();
				// class_interface_associations.add(relation);
				/*
				 * Code to reverse string START
				 */
				String input = relation;
				StringBuilder input1 = new StringBuilder();
				input1.append(input);
				input1 = input1.reverse();

				/*
				 * Code to reverse string END
				 */

				if (((class_interface_associations).contains(input1.toString()))) {

				} else {
					class_interface_associations.add(relation);
					s = s + current_class + "\"1\" -- \"1\"" + n.getType().toString() + "\n";
				}
			} else if ((n.getType().toString().contains("Collection"))) {
				String[] coll;
				coll = n.getType().toString().split("[<>]");
				String relation = current_class + coll[1];
				/*
				 * Code to reverse string START
				 */
				String input = relation;
				StringBuilder reverse_relation = new StringBuilder();
				reverse_relation.append(input);
				reverse_relation = reverse_relation.reverse();

				if ((parser_classes).contains(coll[1])) {
					if (((class_interface_associations).contains(reverse_relation.toString()))) {
					} else {
						class_interface_associations.add(relation);
						s = s + current_class + "\"1\" -- \"*\"" + coll[1] + "\n";
					}
				}
			}
		}
	}
}