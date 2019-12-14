package com.rocket.summer.framework.cglib;


import com.rocket.summer.framework.cglib.beans.BeanCopier;
import com.rocket.summer.framework.cglib.beans.BeanGenerator;
import com.rocket.summer.framework.cglib.beans.BulkBean;
import com.rocket.summer.framework.cglib.beans.ImmutableBean;
import com.rocket.summer.framework.cglib.proxy.CallbackHelper;
import com.rocket.summer.framework.cglib.proxy.Enhancer;
import com.rocket.summer.framework.cglib.proxy.FixedValue;
import com.rocket.summer.framework.cglib.proxy.NoOp;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class SampleClass {
    public String test(String input){
        return "hello world";
    }

    @Test
    public void testFixedValue() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallback(new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return "Hello cglib";
            }
        });
        SampleClass proxy = (SampleClass) enhancer.create();
        System.out.println(proxy.test(null)); //拦截test，输出Hello cglib
        System.out.println(proxy.toString());
        System.out.println(proxy.getClass());
        System.out.println(proxy.hashCode());

    }

    @Test
    public void testCallbackFilter() throws Exception{
        Enhancer enhancer = new Enhancer();
        CallbackHelper callbackHelper = new CallbackHelper(SampleClass.class, new Class[0]) {
            @Override
            protected Object getCallback(Method method) {
                if(method.getDeclaringClass() != Object.class && method.getReturnType() == String.class){
                    return new FixedValue() {
                        @Override
                        public Object loadObject() throws Exception {
                            return "Hello cglib";
                        }
                    };
                }else{
                    return NoOp.INSTANCE;
                }
            }
        };
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallbackFilter(callbackHelper);
        enhancer.setCallbacks(callbackHelper.getCallbacks());
        SampleClass proxy = (SampleClass) enhancer.create();
        assertEquals("Hello cglib", proxy.test(null));
        Assert.assertNotEquals("Hello cglib",proxy.toString());
        System.out.println(proxy.hashCode());
    }

    class SampleBean {
        private String value;

        public SampleBean() {
        }

        public SampleBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    @Test(expected = IllegalStateException.class)
    public void testImmutableBean() throws Exception {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world");
        SampleBean immutableBean = (SampleBean) ImmutableBean.create(bean); //创建不可变类
        assertEquals("Hello world", immutableBean.getValue());
        bean.setValue("Hello world, again"); //可以通过底层对象来进行修改
        assertEquals("Hello world, again", immutableBean.getValue());
        immutableBean.setValue("Hello cglib"); //直接修改将throw exception

    }

    @Test
    public void testBeanGenerator() throws Exception {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.addProperty("value", String.class);
        Object myBean = beanGenerator.create();
        Method setter = myBean.getClass().getMethod("setValue", String.class);
        setter.invoke(myBean, "Hello cglib");

        Method getter = myBean.getClass().getMethod("getValue");
        assertEquals("Hello cglib", getter.invoke(myBean));

    }


    class OtherSampleBean {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testBeanCopier() throws Exception {
        BeanCopier copier = BeanCopier.create(SampleBean.class, OtherSampleBean.class, false);//设置为true，则使用converter
        SampleBean myBean = new SampleBean();
        myBean.setValue("Hello cglib");
        OtherSampleBean otherBean = new OtherSampleBean();
        copier.copy(myBean, otherBean, null); //设置为true，则传入converter指明怎么进行转换
        assertEquals("Hello cglib", otherBean.getValue());
    }


    @Test
    public void testBulkBean() throws Exception {
        BulkBean bulkBean = BulkBean.create(SampleBean.class,
                new String[]{"getValue"},
                new String[]{"setValue"},
                new Class[]{String.class});
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world");
        Object[] propertyValues = bulkBean.getPropertyValues(bean);
        assertEquals(1, bulkBean.getPropertyValues(bean).length);
        assertEquals("Hello world", bulkBean.getPropertyValues(bean)[0]);
        bulkBean.setPropertyValues(bean, new Object[]{"Hello cglib"});
        assertEquals("Hello cglib", bean.getValue());

    }

}
