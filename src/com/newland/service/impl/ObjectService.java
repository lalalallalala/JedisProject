package com.newland.service.impl;

import com.newland.dao.ObjectDao;
import com.newland.model.ServiceObject;
import com.newland.utils.JedisUtil;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class ObjectService {

    static ObjectDao objectDao = new ObjectDao();

    HashMap<String, ServiceObject> type2 = new HashMap();
    HashMap<String, ServiceObject> type3 = new HashMap();
    HashMap<String, ServiceObject> type4 = new HashMap();

    public List<ServiceObject> selectHis(ServiceObject his) {
        System.out.println("开始处理");
        List<ServiceObject> results = new ArrayList<>();

        String date1 = his.getComparisonDate1();
        String date2 = his.getComparisonDate2();
        //取出两个时间点所有的数据
        ServiceObject temp = new ServiceObject();
        temp.setCreateDateString(date1);
        long l = System.currentTimeMillis();
        List<ServiceObject> sos1 = null;

        if(!JedisUtil.exists("ServiceObject1")){
            System.out.println("查询，设置redis");
            sos1 = objectDao.selectByCreateDate(temp);
            String isOk2 = JedisUtil.setObject("ServiceObject1", sos1);
            if("OK".equals(isOk2)){
                System.out.println("设置成功:" + isOk2);
            }
        }else {
            System.out.println("从redis获取");
            sos1 = (List<ServiceObject>)JedisUtil.getObject("ServiceObject1");
        }

        System.out.println("第一次花费时间：" + (System.currentTimeMillis() - l));
        temp.setCreateDateString(date2);
        long l2 = System.currentTimeMillis();
        List<ServiceObject> sos2 = null;

        if(!JedisUtil.exists("ServiceObject2")){
            System.out.println("查询，设置redis-222222222");
            sos2 = objectDao.selectByCreateDate(temp);
            String isOk2 = JedisUtil.setObject("ServiceObject2", sos1);
            if("OK".equals(isOk2)){
                System.out.println("2222222222设置成功:" + isOk2);
            }
        }else {
            System.out.println("从redis获取-222222222");
            sos2 = (List<ServiceObject>)JedisUtil.getObject("ServiceObject2");
        }

        long l3 = System.currentTimeMillis();
        System.out.println("第二次花费时间：" + (l3 - l2));

        //将两组数据根据 type 进行分组（stream，jdk8 特性）
        Map<Integer, List<ServiceObject>> typeObjects1Map =
                sos1.stream().collect(Collectors.groupingBy(ServiceObject::getType));
        Map<Integer, List<ServiceObject>> typeObjects2Map =
                sos2.stream().collect(Collectors.groupingBy(ServiceObject::getType));

        Iterator var11 = ((List)typeObjects1Map.get(2)).iterator();

        ServiceObject o;
        String key;
        while(var11.hasNext()) {
            o = (ServiceObject)var11.next();
            key = o.getObjectName() + o.getParentId();
            this.type2.put(key, o);
        }

        var11 = ((List)typeObjects1Map.get(3)).iterator();

        while(var11.hasNext()) {
            o = (ServiceObject)var11.next();
            key = o.getObjectName() + o.getParentId();
            this.type3.put(key, o);
        }

        var11 = ((List)typeObjects1Map.get(4)).iterator();

        while(var11.hasNext()) {
            o = (ServiceObject)var11.next();
            key = o.getObjectName() + o.getParentId();
            this.type4.put(key, o);
        }

        //用来存放 id，fullName，方便下一级节点取父节点的 fullName，用来拼接当前节点的 fullName
        Map<String, String> idNameMap = new HashMap<>();
        //对第二组数据进行迭代
        typeObjects2Map.forEach((type, os) -> {
            os.forEach(o1 -> {
                //拼接 fullName
                if (o1.getParentId() == null) {
                    o1.setFullName(o1.getObjectName());
                } else {
                    o1.setFullName(idNameMap.get(o1.getParentId()) + "." + o1.getObjectName());
                }
                idNameMap.put(o1.getId(), o1.getFullName());
                //比对
//                long start = System.currentTimeMillis();
                compare(o1, getCompareObj(idNameMap.get(o1.getParentId()), o1.getObjectName(), type, typeObjects1Map), results);
//                System.out.println("花费时间：" + (System.currentTimeMillis() - start));

            });
        });
        long l4 = System.currentTimeMillis();
        System.out.println("总的处理花费时间：" + (l4 - l3));

        return results;
    }

    /**
     * 通过 fullName 在 typeObjects1Map 查找相同节点对象
     *
     * @param parentFullName
     * @param objectName
     * @param type            通过类型将 parentFullName、objectName 分为 服务名、包名、类名、方法名
     * @param typeObjects1Map
     * @return
     */
    private ServiceObject getCompareObj(String parentFullName, String objectName, Integer type,
                                        Map<Integer, List<ServiceObject>> typeObjects1Map) {
        if (type == 1) {
            return typeObjects1Map.get(1).get(0);
        } else if (type == 2) {
            return getCompareObj(parentFullName, objectName, null, null, typeObjects1Map);
        } else if (type == 3) {
            String[] temp = parentFullName.split("\\.");
            return getCompareObj(temp[0], parentFullName.substring(temp[0].length() + 1), objectName, null, typeObjects1Map);
        } else if (type == 4) {
            String[] temp = parentFullName.split("\\.");
            return getCompareObj(temp[0], parentFullName.substring(temp[0].length() + 1, parentFullName.lastIndexOf(".")),
                    temp[temp.length - 1],
                    objectName,
                    typeObjects1Map);
        }
        throw null;
    }

    /**
     * 通过服务名、包名、类名、方法名在 typeObjects1Map 中广度检索需要对象
     *
     * @param serviceName
     * @param packageName
     * @param className
     * @param methodName
     * @param typeObjects1Map
     * @return
     */
    private ServiceObject getCompareObj(String serviceName, String packageName, String className, String methodName,
                                        Map<Integer, List<ServiceObject>> typeObjects1Map) {
        if (serviceName == null || serviceName.length() == 0) {
            return null;
        }
        ServiceObject temp = typeObjects1Map.get(1).get(0);
        if (packageName == null || packageName.length() == 0) {
            return temp;
        }

        String key2 = packageName + temp.getId();
        temp = (ServiceObject)this.type2.get(key2);

//        for (ServiceObject o : typeObjects1Map.get(2)) {
//            if (o.getObjectName().equals(packageName) && o.getParentId().equals(temp.getId())) {
//                temp = o;
//                break;
//            }
//        }

        if (className == null || className.length() == 0) {
            return temp;
        }

        String key3 = className + temp.getId();
        temp = (ServiceObject)this.type3.get(key3);

//        for (ServiceObject o : typeObjects1Map.get(3)) {
//            if (o.getObjectName().equals(className) && o.getParentId().equals(temp.getId())) {
//                temp = o;
//                break;
//            }
//        }
        if (methodName == null || methodName.length() == 0) {
            return temp;
        }
        String key4 = methodName + temp.getId();
        temp = (ServiceObject)this.type4.get(key4);
        
//        for (ServiceObject o : typeObjects1Map.get(4)) {
//            if (o.getObjectName().equals(methodName) && o.getParentId().equals(temp.getId())) {
//                temp = o;
//                break;
//            }
//        }
        return temp;
    }





    private void compare(ServiceObject o, ServiceObject serviceObject, List<ServiceObject> results) {
        if (!o.getIsCoverage().equals(serviceObject.getIsCoverage())) {
            results.add(o);
        }
    }

    public static void main(String[] args) {

        Map<String, Integer> itemsmap = new HashMap<>();
        itemsmap.put("A", 10);
        itemsmap.put("B", 20);
        itemsmap.put("C", 30);
        itemsmap.put("D", 40);
        itemsmap.put("E", 50);
        itemsmap.put("F", 60);

        itemsmap.forEach((a,b)-> System.out.println("K:" + a + "--V:" + b));

//        List items = new ArrayList<>();
//        items.add("A");
//        items.add("B");
//        items.add("C");
//        items.add("D");
//        items.add("E");
//        items.add("B");

//        //遍历方式一
//        items.forEach(System.out::println);
//        //遍历方式二
//        items.forEach(a -> System.out.println(a));
//        //过滤加遍历
//        items.stream().filter(a -> a.equals("B")).forEach(b -> System.out.println(b));

    }


}
