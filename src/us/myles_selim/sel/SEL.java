package us.myles_selim.sel;
import java.io.IOException;
import java.util.Scanner;

public class SEL {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		SELInterpreter interpreter = new SELInterpreter();
		String code = input.nextLine();
		String output;
		try {
			output = interpreter.execute(code);
			System.out.println(output);
		} catch (SELException e) {
			System.out.println(e.getEsotericError());
		} catch (IOException e) {
			e.printStackTrace();
		}
		input.close();
		if (args.length > 0 && args[0] == "-v") {
			System.out.println("DEBUG INFO:" + interpreter.pointer + "," + interpreter.var);
			for (int num : interpreter.data)
				System.out.print(num + "(" + (char) num + ")");
		}
	}

}
