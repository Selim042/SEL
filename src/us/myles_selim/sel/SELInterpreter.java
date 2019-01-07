package us.myles_selim.sel;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SELInterpreter {

	protected List<Integer> data;
	protected int var = 0;
	protected int pointer = 0;

	private InputStreamReader input;

	public SELInterpreter() {
		data = new ArrayList<Integer>();
		input = new InputStreamReader(System.in);
	}

	public static boolean isCodeValid(String code) {
		return code.length() - code.replace("[", "").length() == code.length()
				- code.replace("]", "").length()
				&& (code.length() - code.replace("#", "").length()) % 2 == 0;
	}

	public String execute(String code) throws SELException, IOException {
		if (!isCodeValid(code))
			throw new SELException("Unbalanced [] or #.");
		String output = "";
		for (int codePos = 0; codePos < code.length(); codePos++) {
			if (data.isEmpty())
				data.add(0);
			if (pointer > data.size())
				data.add(pointer, 0);
			// System.out.print(codePos + (code.charAt(codePos) + "."));
			EnumEsotericToken token = EnumEsotericToken.getToken(code.charAt(codePos));
			if (token != null) {
				switch (token) {
				case COMMENT:
					EnumEsotericToken nextToken = EnumEsotericToken.getToken(code.charAt(codePos + 1));
					while (nextToken != EnumEsotericToken.COMMENT) {
						codePos++;
						nextToken = EnumEsotericToken.getToken(code.charAt(codePos));
					}
					break;
				case PREVIOUS_POS:
					pointer--;
					break;
				case NEXT_POS:
					pointer++;
					break;
				case INC:
					var = (var + 1 <= Integer.MAX_VALUE ? var + 1 : Integer.MAX_VALUE);
					break;
				case DEC:
					var = (var - 1 >= 0 ? var - 1 : 0);
					break;
				case ADD:
					var = (var + data.get(pointer) <= Integer.MAX_VALUE ? var + data.get(pointer)
							: Integer.MAX_VALUE);
					break;
				case MIN:
					var = (var - data.get(pointer) >= 0 ? var - data.get(pointer) : 0);
					break;
				case MULT:
					var = (var * data.get(pointer) <= Integer.MAX_VALUE ? var * data.get(pointer)
							: Integer.MAX_VALUE);
					break;
				case DIV:
					var = (var / data.get(pointer) <= Integer.MAX_VALUE ? var / data.get(pointer)
							: Integer.MAX_VALUE);
					break;
				case MOD:
					var %= data.get(pointer);
					break;
				case JUMP_CODE:
					codePos = var;
					break;
				case JUMP_PNT:
					pointer = var;
					break;
				case PRINT:
					output = output + (char) var;
					break;
				case INPUT:
					var = input.read();
					break;
				case SAVE:
					data.add(pointer, var);
					break;
				case LOAD:
					var = data.get(pointer);
					break;
				case EXEC:
					String newCode = "";
					for (int i = pointer; i < pointer + var; i++)
						newCode = newCode + (char) data.get(i).intValue();
					SELInterpreter interpreter = new SELInterpreter();
					interpreter.execute(newCode);
					break;
				case COND:
					var = (var == data.get(pointer) ? 1 : 0);
					break;
				case RESET:
					var = 0;
					break;
				case START_LOOP:
					if (data.get(pointer) == 0) {
						int i = 1;
						while (i > 0) {
							char c2 = code.charAt(codePos + 1);
							if (c2 == EnumEsotericToken.START_LOOP.getChar())
								i++;
							else if (c2 == EnumEsotericToken.END_LOOP.getChar())
								i--;
						}
					}
					break;
				case END_LOOP:
					int i = 1;
					while (i > 0) {
						char c2 = code.charAt(codePos - 1);
						if (c2 == EnumEsotericToken.START_LOOP.getChar())
							i--;
						else if (c2 == EnumEsotericToken.END_LOOP.getChar())
							i++;
					}
					codePos--;
					break;
				default:
					break;
				}
			} else
				throw new SELException("Unrecognized token at " + codePos);
			if (pointer < 0)
				throw new SELException(
						"Error at " + codePos + ": " + "pointer out of bounds: " + pointer);
		}
		return output;
	}

	public enum EnumEsotericToken {
		COMMENT('#'),
		PREVIOUS_POS('<'),
		NEXT_POS('>'),
		INC('i'),
		DEC('d'),
		ADD('+'),
		MIN('-'),
		MULT('*'),
		DIV('/'),
		MOD('%'),
		JUMP_CODE('^'),
		JUMP_PNT('!'),
		PRINT('.'),
		INPUT(','),
		SAVE('$'),
		LOAD('&'),
		EXEC('e'),
		COND('='),
		RESET('r'),
		START_LOOP('['),
		END_LOOP(']');

		private char character;

		EnumEsotericToken(char character) {
			this.character = character;
		}

		@Override
		public String toString() {
			return String.valueOf(this.character);
		}

		public char getChar() {
			return this.character;
		}

		public static EnumEsotericToken getToken(char character) {
			for (EnumEsotericToken token : EnumEsotericToken.values())
				if (token.toString().equals(String.valueOf(character)))
					return token;
			return null;
		}

	}

}
