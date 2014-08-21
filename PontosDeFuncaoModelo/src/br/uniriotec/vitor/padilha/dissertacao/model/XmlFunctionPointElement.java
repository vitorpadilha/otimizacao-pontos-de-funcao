package br.uniriotec.vitor.padilha.dissertacao.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRefs;

import org.xml.sax.Locator;

import br.uniriotec.vitor.padilha.dissertacao.exception.CloneException;
import br.uniriotec.vitor.padilha.dissertacao.utils.ClassUtils;

import com.sun.xml.internal.bind.annotation.XmlLocation;


public class XmlFunctionPointElement  implements Cloneable {
	
	@XmlLocation
	private Locator location;
	public static Map<Object,Object> clones = new HashMap<Object, Object>();
	@SuppressWarnings("rawtypes")
	public static Map<Class,Map<String,Method>> classGetMethods = new HashMap<Class, Map<String,Method>>();
	@SuppressWarnings("rawtypes")
	public static Map<Class,Map<String,Method>> classSetMethods = new HashMap<Class, Map<String,Method>>();
	
	public Locator getLocation() {
		return location;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public XmlFunctionPointElement cloneElement() throws CloneException {		
		try {
			Map<String,Method> metodos=classGetMethods.get(this.getClass());
			Map<String,Method> metodosSet=classSetMethods.get(this.getClass());
			if(metodos==null){
				Map<String,Method> metodosDaClasse = ClassUtils.getClassPublicMethods(this.getClass());
				classGetMethods.put(this.getClass(), new HashMap<String, Method>());
				classSetMethods.put(this.getClass(), new HashMap<String, Method>());
				for(Method metodo:metodosDaClasse.values()) {
					Class retorno = metodo.getReturnType();			
					if(!retorno.isPrimitive() && !Modifier.isStatic(retorno.getModifiers())) {
						if(metodo.getName().startsWith("get") && !metodo.getName().equals("getParent") && !metodo.getName().equals("getStakeholderInterests")
								&& (
										metodo.isAnnotationPresent(XmlElement.class)
										|| metodo.isAnnotationPresent(XmlAttribute.class)
										|| metodo.isAnnotationPresent(XmlElementRefs.class)
										//|| metodo.isAnnotationPresent(XmlTransient.class)
										)								
								) {
							String metodoSet = "set"+metodo.getName().substring(3,metodo.getName().length());
							if(metodosDaClasse.containsKey(metodoSet)){
								classGetMethods.get(this.getClass()).put(metodo.getName(), metodo);
								classSetMethods.get(this.getClass()).put(metodo.getName(), metodosDaClasse.get(metodoSet));
							}
						}
					}
				}
				metodos = classGetMethods.get(this.getClass());
				metodosSet = classSetMethods.get(this.getClass());
			}
				
			XmlFunctionPointElement objeto = (XmlFunctionPointElement) super.clone();
			for(Method metodo:metodos.values()) {
				Object valor = metodo.invoke(objeto, null);					
				Method metodoSet = metodosSet.get(metodo.getName());
				if (valor instanceof Collection) {			
					Collection collection = (Collection) valor.getClass().newInstance();					
					for(Object objectoClone:((Collection)valor)) {
						XmlFunctionPointElement elemento = verifyElement(metodoSet, objectoClone, null,objeto);
						if(elemento!=null)
							collection.add(elemento);
					}
					metodoSet.invoke(objeto, collection);
					//System.out.println("2"+metodo.getName());
				}
				else {
					verifyElement(metodoSet, valor, metodo, objeto);
				}			
			}
			return objeto;
		} catch (CloneNotSupportedException e) {
			throw new CloneException();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new CloneException();
		} catch (IllegalAccessException e) {
			throw new CloneException();
		} catch (InvocationTargetException e) {
			throw new CloneException();
		} catch (InstantiationException e) {
			throw new CloneException();
		}	
	}
	
	private XmlFunctionPointElement verifyElement(Method metodoSet, Object valor, Method metodo, XmlFunctionPointElement objeto) throws CloneException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if(valor instanceof XmlFunctionPointElement) {
			if(clones.containsKey(valor))
				return (XmlFunctionPointElement) clones.get(valor);
			XmlFunctionPointElement objectoClone = ((XmlFunctionPointElement)valor).cloneElement();
			clones.put(valor, objectoClone);
			if(objectoClone instanceof XmlFunctionPointElementWithParent)
				((XmlFunctionPointElementWithParent)objectoClone).setParent(objeto);
			if(metodo!=null) {
				metodoSet.invoke(objeto, objectoClone);
				//System.out.println("1"+metodo.getName());
			}			
			return objectoClone;
		}
		return null;
	}
}
