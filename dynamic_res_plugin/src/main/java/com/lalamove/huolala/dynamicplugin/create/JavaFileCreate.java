package com.lalamove.huolala.dynamicplugin.create;

import com.lalamove.huolala.dynamicbase.DynamicConst;
import com.lalamove.huolala.dynamicbase.DynamicResType;
import com.lalamove.huolala.dynamicbase.SoType;
import com.lalamove.huolala.dynamicbase.bean.AbsResInfo;
import com.lalamove.huolala.dynamicbase.bean.DynamicPkgInfo;
import com.lalamove.huolala.dynamicbase.bean.DynamicSoInfo;
import com.lalamove.huolala.dynamicbase.util.TextUtil;
import com.lalamove.huolala.dynamicplugin.DynamicParam;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: JavaFileCreate
 * @author: huangyuchen
 * @date: 4/19/22
 * @description:
 * @history:
 */
public class JavaFileCreate implements IDynamicFileCreate {


    private Map<DynamicResType, String> dynamicTypeMap;
    private Map<String, String> soTypeMap;


    public JavaFileCreate() {

        createDynamicTypeMap();
        createSoTypeMap();
    }

    private void createDynamicTypeMap() {
        dynamicTypeMap = new HashMap<>();
        String pkgName = DynamicResType.class.getCanonicalName() + ".";
        dynamicTypeMap.put(DynamicResType.TYPEFACE, pkgName + "TYPEFACE");
        dynamicTypeMap.put(DynamicResType.FRAME_ANIM, pkgName + "FRAME_ANIM");
        dynamicTypeMap.put(DynamicResType.SO, pkgName + "SO");
        dynamicTypeMap.put(DynamicResType.ZIP, pkgName + "ZIP");
        dynamicTypeMap.put(DynamicResType.SINGLE, pkgName + "SINGLE");
    }

    private void createSoTypeMap() {
        soTypeMap = new HashMap<>();
        String pkgName = SoType.class.getCanonicalName() + ".";
        soTypeMap.put(SoType.ARM64_V8A.name, pkgName + "ARM64_V8A");
        soTypeMap.put(SoType.ARMEABI.name, pkgName + "ARMEABI");
        soTypeMap.put(SoType.ARMEABI_V7A.name, pkgName + "ARMEABI_V7A");
        soTypeMap.put(SoType.X86.name, pkgName + "X86");
        soTypeMap.put(SoType.X86_64.name, pkgName + "X86_64");
    }

    @Override
    public void createFile(List<DynamicPkgInfo> pkgs, DynamicParam param) {
        if (pkgs == null || pkgs.size() == 0 || param == null) {
            return;
        }


        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder("DynamicResConst")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        List<SoInfo> soList = new ArrayList<>();
        for (DynamicPkgInfo pkg : pkgs) {
            FieldSpec fsc = createDynamicPkgField(pkg, soList);
            typeBuilder.addField(fsc);
        }
        List<FieldSpec> fieldList = createSoFieldList(soList);
        if (fieldList != null && !fieldList.isEmpty()) {
            for (FieldSpec fsc : fieldList) {
                typeBuilder.addField(fsc);
            }
        }
        JavaFile javaFile = JavaFile.builder(param.getCreateJavaPkgName(), typeBuilder.build())
                .build();
        try {
            javaFile.writeTo(new File(param.getOutputPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建DynamicSoInfo常量列表
     *
     * @param soList
     * @return
     */
    private List<FieldSpec> createSoFieldList(List<SoInfo> soList) {
        List<FieldSpec> fieldList = new ArrayList<>();
        if (soList == null || soList.size() == 0) {
            return fieldList;
        }

        Map<String, List<SoInfo>> map = new HashMap<>();
        //遍历所有so资源
        for (SoInfo soInfo : soList) {
            //获取不带abi的资源id，即hll_abi1_so->hll_so
            //这样我们就把所有hll_abi_so的资源，分类到map的同一个位置
            //key为hll_so，值为下面各个abi资源
            String id = getSoIdWithoutAbi(soInfo);
            if (!TextUtil.isEmpty(id)) {
                List<SoInfo> list = map.get(id);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(id, list);
                }
                list.add(soInfo);
            }
        }

        for (Map.Entry<String, List<SoInfo>> entry : map.entrySet()) {
            FieldSpec fsc = createSoField(entry);
            fieldList.add(fsc);
        }
        return fieldList;
    }

    /**
     * 创建单独一个DynamicSoInfo常量
     *
     * @param entry
     * @return
     */
    private FieldSpec createSoField(Map.Entry<String, List<SoInfo>> entry) {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("new $T ( \r\n", DynamicSoInfo.class);
        int length = entry.getValue().size();
        int index = 0;
        for (SoInfo soInfo : entry.getValue()) {
            builder.add("new $T ( \r\n", DynamicSoInfo.DynamicAbiInfo.class);
            builder.add("$L , ", soTypeMap.get(soInfo.abi));
            builder.add("$L )", soInfo.field.name);
            if (index != length - 1) {
                builder.add(",\r\n");
            }
            index++;
        }
        builder.add(")");
        CodeBlock cb = builder.build();
        String name = entry.getKey();
        name = TextUtil.getValidJavaFieldName(name);
        name = name.replace("__", "_");
        FieldSpec fsc = FieldSpec.builder(DynamicSoInfo.class, name)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer(cb)
                .build();
        return fsc;
    }

    /**
     * 获取so资源id，不附带abi名称
     * 目的时将hll_abi1_so与hll_abi2_so等多种包含abi的压缩包分类到一起
     *
     * @param info
     * @return
     */
    private String getSoIdWithoutAbi(SoInfo info) {
        if (info == null || info.pkg == null) {
            return "";
        }
        String id = info.pkg.getId();
        if (TextUtil.isEmpty(id)) {
            return "";
        }
        return id.replace(info.abi, "");
    }

    private FieldSpec createDynamicPkgField(DynamicPkgInfo pkg, List<SoInfo> soList) {
        String name = TextUtil.getValidJavaFieldName(pkg.getName());
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("new $T ( \r\n", DynamicPkgInfo.class)
                .add("$S, \r\n", pkg.getId())
                .add("$S, \r\n", pkg.getName())
                .add("$L, \r\n", dynamicTypeMap.get(pkg.getType()))
                .add("$L, \r\n", pkg.getSubType())
                .add("$S, \r\n", pkg.getUrl())
                .add("$L, \r\n", pkg.getVersion())
                .add("$L, \r\n", pkg.getLength());
        Map<String, AbsResInfo> map = pkg.getMap();
        if (map == null || map.size() == 0) {
            builder.add("$S )", pkg.getMd5());
        } else {
            builder.add("$S, \r\n", pkg.getMd5());
            for (AbsResInfo subInfo : pkg.getMap().values()) {
                createSubField(subInfo, builder);
            }
            builder.add(")");
        }
        Modifier modifer = Modifier.PUBLIC;
        if (pkg.getType() == DynamicResType.SO) {
            modifer = Modifier.PRIVATE;
        }
        CodeBlock cb = builder.build();
        FieldSpec fsc = FieldSpec.builder(DynamicPkgInfo.class, name)
                .addModifiers(modifer, Modifier.FINAL, Modifier.STATIC)
                .initializer(cb)
                .build();
        if (pkg.getType() == DynamicResType.SO) {
            soList.add(new SoInfo(pkg, fsc));
        }
        return fsc;
    }

    private void createSubField(AbsResInfo info, CodeBlock.Builder builder) {
        if (info.getVerifyType() == DynamicConst.VerifyType.FOLDER) {
            Map<String, AbsResInfo> subMap = info.getMap();
            builder.add("new $T ( ", DynamicPkgInfo.FolderInfo.class);
            builder.add("$S,\r\n", info.getName());
            Collection<AbsResInfo> collecs = subMap.values();
            int size = collecs.size();
            int index = 0;
            List<AbsResInfo> list = new ArrayList<>(subMap.values());
            Collections.sort(list, new Comparator<AbsResInfo>() {
                @Override
                public int compare(AbsResInfo o1, AbsResInfo o2) {
                    return (int) (o2.getLength() - o1.getLength());
                }
            });
            for (AbsResInfo subInfo : list) {
                createSubField(subInfo, builder);
                if (index != size - 1) {
                    builder.add(",\r\n");
                }
                index++;
            }
            builder.add(")");
        } else if (info.getVerifyType() == DynamicConst.VerifyType.FILE) {
            builder.add("new $T ( ", DynamicPkgInfo.FileInfo.class)
                    .add("$S,", info.getName())
                    .add("$S,", info.getMd5())
                    .add("$L)", info.getLength());
        }
    }

    class SoInfo {
        public final DynamicPkgInfo pkg;
        public final FieldSpec field;
        public String abi;

        SoInfo(DynamicPkgInfo pkg, FieldSpec field) {
            this.pkg = pkg;
            this.field = field;
            for (String name : soTypeMap.keySet()) {
                if (pkg.getName().contains(name)) {
                    abi = name;
                    break;
                }
            }
        }
    }
}
