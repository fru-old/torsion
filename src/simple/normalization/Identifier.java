package simple.normalization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.github.fru.torsion.main.B;

public class Identifier {

	public static Class<?>[] parseMethodSignatureConstant(String constant) {
		ArrayList<Class<?>> out = new ArrayList<Class<?>>();
		char first = constant.charAt(0);
		int middle = constant.indexOf(')');
		if (first != '(' || middle < 0 || middle < 1 || middle > constant.length()) {
			throw new RuntimeException("Could not parse parameter");
		}
		try {
			String in = constant.substring(1, middle);
			for (int i = 0; i < in.length(); i++) {
				char c = in.charAt(i);
				if (c == 'L') {
					int start = i + 1;
					while (i < in.length() && in.charAt(i) != ';')
						i++;
					int end = i;
					String clazz = in.substring(start, end).replace('/', '.');
					out.add(Class.forName(clazz));

				}else{
					switch(c){
						case 'V': out.add(Void.TYPE); break;
						case 'I': out.add(int.class); break;
						case 'J': out.add(long.class); break;
						case 'F': out.add(float.class); break;
						case 'D': out.add(double.class); break;
						case 'B': out.add(byte.class); break;
						case 'C': out.add(char.class); break;
						case 'S': out.add(short.class); break;
						case 'Z': out.add(boolean.class); break;
					}
				}
			}
			return out.toArray(new Class<?>[out.size()]);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	public static enum Type{
		METHOD, FIELD, ID
	}
	
	public final Type type;
	public final Method method;
	public final Field field;
	public final Object id;
	
	public Identifier(String name, Class<?> clazz, String signature){
		try{
			this.type = Type.METHOD;
			this.method = clazz.getMethod(name, parseMethodSignatureConstant(signature));
			this.field = null;
			this.id = null;
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}	
	}
	
	public Identifier(String name, Class<?> clazz){
		try{
			this.type = Type.FIELD;
			this.method = null;
			this.field = clazz.getDeclaredField(name);
			this.id = null;
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}
	}
	
	public Identifier(Object object){
		this.type = Type.ID;
		this.method = null;
		this.field = null;
		this.id = object;
	}
	
	public Identifier(){
		this(new Object());
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Identifier))return false;
		Identifier other = (Identifier)o;
		return this.field == other.field && this.id == other.id && this.method == other.method;
	}
	
	@Override
	public int hashCode(){
		if(this.field != null)return this.field.hashCode();
		if(this.method != null)return this.method.hashCode();
		if(this.id != null)return this.id.hashCode();
		return this.hashCode();
	}
	
	//Normalization: Operation StackAssignment

	public static void main(String[] args) throws Exception {
		parseMethodSignatureConstant("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;");
		System.out.println(Class.forName("java.lang.Integer"));
		System.out.println(Class.forName("java.lang.Integer").getMethod("toString", parseMethodSignatureConstant("()Ljava/lang/String;")));
		System.out.println(B.class.getMethod("test", parseMethodSignatureConstant("(Ljava/lang/Object;Ljava/lang/Object;II)Ljava/lang/String;")));
		System.out.println(B.class.getDeclaredField("a"));
		// 

	}
}
