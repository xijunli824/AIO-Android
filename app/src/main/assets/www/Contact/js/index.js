
$(document).bind("mobileinit",function(){
                 $.mobile.ajaxEnabled=true;
                 //$.mobile.loadingMessageTextVisible = true;
                 }
                 );
$(document).bind("pageshow",function(e,ui){
                 switch(e.target.id){
                 case "main":
                 //刚打开app时才调用
                 if($("#listView>li").length===0){
                 
                 setTimeout(getUserInfo,100);
                 //setTimeout(getxml,100);
                 }
                    break;
                 case "depts":
                    setDeptList(app.getPara("CorpId"),app.getPara("tokenkey"));
                    break;
                 case "contactList":
                    getContactList(app.getPara("deptId"));
                    break;
                 case "contactDetail":
                    getContactDetail(app.getPara("contactId"));
                    break;
                 }
                 });
$(document).bind("pagebeforeshow",function(e,ui){
                 
                 switch(e.target.id){
                 case "depts":
                    $("#id-depts-title").text(unescape(decodeURIComponent(app.getPara("name"))));
                    break;
                 case "contactList":
                    $("#id-contactList-title").text(unescape(decodeURIComponent(app.getPara("deptName"))));
                    break;
                 }
                 });
$(document).bind("pagebeforechange",function(){
                 //$.mobile.showPageLoadingMsg('a', "Please wait...");
                 });

//获取当前url的参数
function getPara(name){
    var url=window.location.href,paras="";
    paras=url.replace(url.match(/(.*|\w*\d)\?/gi),"");
    paras=paras.match(new RegExp(name+"=([^&])*", "gi")).toString();
    return paras.replace(new RegExp(name+"{1}=","gi"),"");
}
//获取用户信息
function getUserInfo(){
    Cordova.exec(getUserInfoSuc,null,"UserInfo", "GetUserInfo",[]);
    
}
function getUserInfoSuc(result){
    var tokenkey= result.userToken;
    getAreaAndMarket(tokenkey);
    
}

function getjson(result){
    
    alert(JSON.parse(result)[0].Zones);
    //var obj = eval( "(" + result + ")" );//转换后的JSON对象
    //alert(obj.Zones);//json name
}
//获取组织结构
function getAreaAndMarket(tokenkey){
   
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:"http://192.168.1.120/api/GetZoneCorpList",
           type:"POST",
           data:{"tokenkey":tokenkey},
           dataType:"json",
           success:function(cl){
           
          // Cordova.exec(null,null,"UserInfo", "GetUserInfo",[]);
//           Cordova.exec(null,null,"UserInfo","WriteCacheInfo",["keystrjson",JSON.stringify(cl)]);
//          
//           Cordova.exec(getjson,null,"UserInfo","GetCacheInfo",["keystrjson"]);
           oListView=$("#listView"),
           childs=[];
           oListView.html("");
           
           for(var i=0;i<cl.length;i++){
           for(var j=0;j<cl[i].Area.length;j++){
           var areaObj=cl[i].Area[j];
           
           for(var k=0;k<areaObj.CorpList.length;k++){
           
           var corpObj = areaObj.CorpList[k];
           
           childs.push("<li class=\"area_"+j+"\" style=\"overflow:hidden;display:block;\" data-transition=\"slide\" data-icon=\"false\"><a href=\"views/depts.html?CorpId="+corpObj.CorpId+"&tokenkey="+tokenkey+"&name="+encodeURIComponent(corpObj.Corps)+"\" data-transition=\"slide\"><div class=\"ui-listview-row-title\">"+corpObj.Corps+"</div><div class=\"ui-listview-row-content\">"+"</div></a></li>");
           }
           oListView.append("<li data-role=\"list-divider\" onClick=\"hideArea('area_"+j+"')\"><input id =\"area_"+j+"\" value =\"show\" type=\"hidden\" />"+areaObj.Zones+"</li>");
           oListView.append(childs);
           childs=[];
           }
           }
           oListView.append(childs.join(""));
           oListView.listview("refresh");
           
           for(var i=0;i<cl[0].Area.length;i++){
           hideArea("area_"+i+"");
           }
           $.mobile.hidePageLoadingMsg();
           },
           error: function(xhr,textStatus,errMsg) {
           
           alert(textStatus);
           
           }
           });
}

function getContacts(){

    oListView=$("#listView"),
    childs=[];
    oListView.html("");

    if(false)
    {
        // alert(localStorage.getItem(contactsJSON));
        var strToJson=JSON.parse(localStorage.contactsJSON||"[]");
        //alert(strToJson);
      
        for(var i=0;i<strToJson.length;i++){
            var areaObj = strToJson[i];
            
            for(var j=0;j<areaObj.CorpList.length;j++){
                var corpObj = areaObj.CorpList[j];
                var alldept = encodeURIComponent(JSON.stringify(corpObj.Dept));
                childs.push("<li class=\"area_"+i+"\" style=\"overflow:hidden;display:block;\" data-transition=\"slide\" data-icon=\"false\"><a href=\"views/depts.html?deptList="+alldept+"&name="+encodeURIComponent(corpObj.Corps)+"\" data-transition=\"slide\"><div class=\"ui-listview-row-title\">"+corpObj.Corps+"</div><div class=\"ui-listview-row-content\">"+"</div></a></li>");
            }
            oListView.append("<li data-role=\"list-divider\" onClick=\"hideArea('area_"+i+"')\"><input id =\"area_"+i+"\" value =\"show\" type=\"hidden\" />"+areaObj.Zones+"</li>");
            oListView.append(childs);
            childs=[];
        }
        
        oListView.append(childs.join(""));
        oListView.listview("refresh");
        for(var i=0;i<cl.length;i++){
            hideArea("area_"+i+"");
        }
//        alert("www");
    }else{
//        alert("ddd");
        getAreaAndMarket();
    }
}

function hideArea(name){
    if($("#"+name).attr("value") === "show"){
        $("."+name).hide(100,function(){$("#"+name).attr("value","hidden");});
    }
    else{
        $("."+name).show(100,function(){$("#"+name).attr("value","show");});
    }
}

window.app ={
    getPara:getPara,
getContacts:getContacts,
getAreaAndMarket:getAreaAndMarket
};