// 定义服务层:
app.service("brandService",function($http){
	this.findAll = function(){
		return $http.get("../brand/findAll.do");
	}
	
	this.findByPage = function(page,rows){
		return $http.get("../brand/findPage.do?page="+page+"&rows="+rows);
	}
	
	this.save = function(brand){
		return $http.post("../brand/insert.do",brand);
	}
	
	this.update=function(brand){
		return $http.post("../brand/update.do",brand);
	}
	
	this.findById=function(id){
		return $http.get("../brand/findOne.do?id="+id);
	}
	
	this.dele = function(ids){
		return $http.get("../brand/delete.do?ids="+ids);
	}
	
	this.search = function(page,rows,searchEntity){
		return $http.post("../brand/find.do?page="+page+"&rows="+rows,searchEntity);
	}
	
	this.selectOptionList = function(){
		return $http.get("../brand/list.do");
	}
});