package dataobject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class ObjectHelper
{
	public static String objectToString(Serializable obj)
	{
		String result = null;
		try
		{
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(obj);
			so.flush();
			result = new String(Base64.encode(bo.toByteArray()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T stringToObject(String str)
	{
		T ret = null;
		try
		{
			byte b[] = Base64.decode(str.getBytes());
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			ret = (T)si.readObject();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
}
