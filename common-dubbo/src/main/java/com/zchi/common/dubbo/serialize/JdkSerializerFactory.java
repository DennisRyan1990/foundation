package com.zchi.common.dubbo.serialize;

import com.alibaba.com.caucho.hessian.io.*;
import java.io.*;
import java.util.regex.Pattern;

public class JdkSerializerFactory extends AbstractSerializerFactory {

    private Pattern pattern;

    public JdkSerializerFactory(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (Serializable.class.isAssignableFrom(cl) && pattern.matcher(cl.getName()).matches()) {
            return new AbstractSerializer() {
                @Override public void writeObject(Object obj, AbstractHessianOutput out)
                    throws IOException {
                    if (obj == null) {
                        out.writeNull();
                        return;
                    }
                    try (
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(buffer)) {
                        oos.writeObject(obj);
                        out.writeBytes(buffer.toByteArray());
                    }

                }
            };
        }
        return null;
    }

    @Override public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (Serializable.class.isAssignableFrom(cl) && pattern.matcher(cl.getName()).matches()) {
            return new AbstractDeserializer() {
                @Override public Object readObject(AbstractHessianInput in) throws IOException {
                    byte[] bytes = in.readBytes();
                    try (
                        ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
                        ObjectInputStream ois = new ObjectInputStream(buffer)) {
                        return ois.readObject();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException();
                    }
                }
            };
        }
        return null;
    }
}

