package com.offcn.core.bean;

import com.offcn.core.pojo.specification.Specification;
import com.offcn.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;

//添加规格时的实体，
public class SpecEntity implements Serializable {
    //规格的实体
    private Specification specification;
    //规格选项实体
    private List<SpecificationOption> specificationOptionList;


    public SpecEntity() {

    }
    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
