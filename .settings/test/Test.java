import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.cglib.beans.BeanMap;



/**
 *@author Kevin.yang
 *@since 2015年5月17日
 */
public class Test {

	public static void main(String[] args) throws Exception {
		A a = new A();
		BeanMap b = BeanMap.create(a);
		TypeVariable[] t = (b.getPropertyType("l").getTypeParameters());
		//ParameterizedType p = (ParameterizedType) PropertyUtils.getPropertyDescriptor(a, "l").getReadMethod().;
		System.out.println(t[0].getBounds()[0]);
	}
	

}

class A {
	private List<String> l;

	public List<String> getL() {
		return l;
	}

	public void setL(List<String> l) {
		this.l = l;
	}
	
	
}
