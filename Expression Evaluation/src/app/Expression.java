package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is
	 * created and stored, even if it appears more than once in the expression. At
	 * this time, values for all variables and all array items are set to zero -
	 * they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr   The expression
	 * @param vars   The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		/**
		 * DO NOT create new vars and arrays - they are already created before being
		 * sent in to this method - you just need to fill them in.
		 **/
		expr = expr.replaceAll("\\s", "");
		String delimiters = delims + "1234567890";
		for (int i = 0; i < expr.length(); i++) {
			if (delimiters.indexOf(expr.charAt(i)) < 0) { // if the char is not a delimiter
				String name = "";
				int index = 0;
				for (int j = i; j < expr.length(); j++) { // continues till end of variable/array name
					if (delimiters.indexOf(expr.charAt(j)) >= 0) {
						i += name.length();
						if (expr.charAt(j) == '[')
							index = j; // stores the location of the last
						break;
					} else {
						name += expr.charAt(j);
					}
				}
				if (index != 0) { // expr.charAt(index + 1) == '['
					Array arr = new Array(name);
					arrays.add(arr);
				} else {
					Variable var = new Variable(name);
					vars.add(var);
				}
			}

		}
		for (int i = 0; i < arrays.size(); i++) {
			System.out.println(arrays.get(i));
		}

	}

	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input
	 * @param vars   The variables array list, previously populated by
	 *               makeVariableLists
	 * @param arrays The arrays array list - previously populated by
	 *               makeVariableLists
	 */
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars   The variables array list, with values for all variables in the
	 *               expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	private static String getVar(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		StringTokenizer st = new StringTokenizer(expr, delims);
		String str = "";
		while (st.hasMoreTokens()) {
			try {
				str = st.nextToken();
				Float.parseFloat(str);
				

			} catch (NumberFormatException e) {

				if (vars.contains(new Variable(str)) && !arrays.contains(new Array(str))) {
					expr = expr.substring(0, expr.indexOf(str)) + vars.get(vars.indexOf(new Variable(str))).value
							+ expr.substring(expr.indexOf(str) + str.length(), expr.length());
				}
			}
		}
		return expr;
	}

	private static String evalBrack(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		StringTokenizer st;
		while (expr.indexOf('[') != -1) {

			st = new StringTokenizer(expr, "[]");
			while (st.hasMoreTokens()) {
				String str = st.nextToken();
				try {
					float x = eval(str);
					str = "[" + str + "]";
					expr = expr.substring(0, expr.indexOf(str)) + (int) x
							+ expr.substring(expr.indexOf(str) + str.length());
				} catch (Exception e) {
					// until exception is caught
				}
			}
			System.out.println(expr);
			st = new StringTokenizer(expr, delims);
			while (st.hasMoreTokens()) {
				String str = st.nextToken();
				System.out.println(str);
				if (arrays.contains(new Array(str))) {
					continue;
				} else {
					try {
						Float.parseFloat(str);

					} catch (Exception e) {
						String str2 = str;
						String[] splitDigPos = str.split("(?<=\\D)(?=\\d)");
						float x = Float.parseFloat(splitDigPos[1]);
						expr = expr.substring(0, expr.indexOf(str2))
								+ arrays.get(arrays.indexOf(new Array(splitDigPos[0]))).values[(int) x]
								+ expr.substring(expr.indexOf(str2) + str2.length());

					}

				}
			}

		}
		return expr;
	}

	private static ArrayList<Character> ops = new ArrayList<Character>();
	private static ArrayList<Float> numeric = new ArrayList<Float>();

	private static void reorder(String input, int index) throws IllegalArgumentException {
		if ((index <= input.length() - 2) && (input.charAt(index + 1) != '-') && (input.charAt(index) == '+'
				|| input.charAt(index) == '*' || input.charAt(index) == '/' || input.charAt(index) == '-')) {

			if (index == 0 && input.charAt(index) == '-') {
				index++;
				while (index != input.length() && !(input.charAt(index) == '+' || input.charAt(index) == '*'
						|| input.charAt(index) == '/' || input.charAt(index) == '-')) {
					index++;
				}

				try {
					numeric.add(Float.parseFloat(input.substring(0, index)));
					reorder(input, index);
				} catch (Exception e) {

					throw new IllegalArgumentException("incorrect syntax");
				}

			} else {
				ops.add(input.charAt(index));
				reorder(input, index + 1);
			}
		} else if ((index <= input.length() - 2) && (input.charAt(index) == '+' || input.charAt(index) == '*'
				|| input.charAt(index) == '/' || input.charAt(index) == '-') && input.charAt(index + 1) == '-') {

			ops.add(input.charAt(index));
			index += 1;
			int start = index;//2
			index++;//3
			while (index != input.length() && !(input.charAt(index) == '+' || input.charAt(index) == '*'
					|| input.charAt(index) == '/' || input.charAt(index) == '-')) {
				index++;
			}

			try {
				numeric.add(Float.parseFloat(input.substring(start, index)));
				reorder(input, index);
			} catch (Exception e) {

				throw new IllegalArgumentException("incorrect syntax");
			}
		} else if (index < input.length()) {
			int start = index;
			while (index != input.length() && !(input.charAt(index) == '+' || input.charAt(index) == '*'
					|| input.charAt(index) == '/' || input.charAt(index) == '-')) {
				index++;
			}
			try {
				numeric.add(Float.parseFloat(input.substring(start, index)));
				reorder(input, index);
			} catch (Exception e) {

				throw new IllegalArgumentException("incorrect syntax");
			}

		}

	}

	private static float priorityMath(ArrayList<Float> opands, ArrayList<Character> oper)
			throws IllegalArgumentException {
		int index = 0;

		if (oper.contains('*') && (oper.indexOf('/') == -1 || oper.indexOf('*') < oper.indexOf('/'))) {
			index = oper.indexOf('*');
			opands.set(index, opands.get(index) * opands.get(index + 1));
			opands.remove(index + 1);
			oper.remove(index);
			
			return priorityMath(opands, oper);
		} else if (oper.contains('/') && (oper.indexOf('*') == -1 || oper.indexOf('*') > oper.indexOf('/'))) {
			index = oper.indexOf('/');
			if (opands.get(index + 1) != 0) {
				opands.set(index, opands.get(index) / opands.get(index + 1));
				opands.remove(index + 1);
				oper.remove(index);

				return priorityMath(opands, oper);
			} else {
				throw new IllegalArgumentException("div by 0");
			}

		} else if ((oper.contains('+')) && (oper.indexOf('-') == -1 || oper.indexOf('+') < oper.indexOf('-'))) {
			index = oper.indexOf('+');
			opands.set(index, opands.get(index) + opands.get(index + 1));
			opands.remove(index + 1);
			oper.remove(index);

			return priorityMath(opands, oper);
		} else if ((oper.contains('-')) && (oper.indexOf('+') == -1 || oper.indexOf('+') > oper.indexOf('-'))) {
			index = oper.indexOf('-');
			opands.set(index, opands.get(index) - opands.get(index + 1));
			opands.remove(index + 1);
			oper.remove(index);

			return priorityMath(opands, oper);
		} else if (oper.size() == 0 || opands.size() == 1) {

			return opands.get(0);
		} else {

			throw new IllegalArgumentException("incorrect syntax");
		}
	}

	private static String evalParen(String input) {
		if (input.lastIndexOf('(') != -1) {
			String x = input.substring(input.lastIndexOf('(') + 1, input.indexOf(')', input.lastIndexOf('('))); 
			reorder(x, 0);
			float y = priorityMath(numeric, ops);
			numeric.removeAll(numeric);
			ops.removeAll(ops);
			input = input.substring(0, input.lastIndexOf('(')) + y
					+ input.substring(input.indexOf(')', input.lastIndexOf('(')) + 1, input.length());

			return evalParen(input);
		} else {
			return input;
		}

	}

	private static float eval(String input) {

		input = evalParen(input);

		numeric.removeAll(numeric);
		ops.removeAll(ops);
		reorder(input, 0);
		float y = priorityMath(numeric, ops);
		numeric.removeAll(numeric);
		ops.removeAll(ops);
		return y;

	}

	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		// following line just a placeholder for compilation
		expr = expr.replaceAll(" ", "");
		expr = expr.replaceAll("\t", "");
		expr = getVar(expr, vars, arrays);
		expr = evalBrack(expr, vars, arrays);
		return eval(expr);
	}

}
