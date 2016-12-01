var pageNumber,
    pageType,
    currentpage,
    pendingTaskcount,
    isCN;

$(document).bind("mobileinit",function(){
                 $.mobile.ajaxEnabled=true;
                

                 //setTimeout(getUserInfo,100);
                 //$.mobile.loadingMessageTextVisible = true;
                 }
                 );



$(document).bind("pageshow",function(e,ui){
				currentpage=e.target.id;
                 
                 switch(e.target.id){
                 case "main":
                 //刚打开app时才调用
                 if($("#listView>li").length===0){
                 
                 localStorage.setItem("pendingTaskcount",-1);
//                 setTimeout(getUserInfo,100);
                
                 }else{
                 if(pageType=="Pending"){
//                    getPendingList(0);
                    getPendingListNew("1");
                 
                 }else if(pageType=="Complete"){
//                    getCompleteList(0);
                    getCompleteListNew("1");
                
                 }

                 
                 getPendingCount(0,1000);
                 }
                 break;
                 case "taskDetails":{
                 
                 getTask(app.getPara("SerialNumber"));
                 break;}
                 case "taskCompleteDetails":{
                 
                 getTask(app.getPara("SerialNumber"));
                 break;
                 }
                 
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






//返回到cmamovile主界面
function exit(){
//    Cordova.exec(null,null,"Task", "exit",[]);
//    console.log("count:"+localStorage.getItem("pendingTaskcount"));
    Cordova.exec(function(result){
                 //                 localStorage.removeItem("UnreadNoticeCount");
                 },null,"Application", "exit",[localStorage.getItem("pendingTaskcount"),"-1","NO",localStorage.getItem("AppID")]);
}
document.addEventListener("deviceready", function(){
                          Cordova.exec(function(result){
                                       localStorage.setItem("AppID",result.appID);
                                       localStorage.setItem("userToken",result.userToken);
                                       localStorage.setItem("userName",result.userName );
                                       localStorage.setItem("userPwd",result.userPassword);
                                       localStorage.setItem("language",result.language);
                                       isCN = result.language == "中文"?true:false;
                                       console.log("userName"+result.userName);
                                       console.log("userPwd"+result.userPassword);
                                       if(result.userName.substring(0,2)=="w_" ||result.userName.substring(0,2)=="W_"){
                                           
                                           navigator.notification.alert(isCN?"无权限访问！":"No permission to access!",exit,isCN?"提示":"Prompt",isCN?"确定":"OK");
                                           
                                           }
                                       //localStorage.setItem("serverUrlimoffice","http://10.154.128.13");
//                                       localStorage.setItem("serverUrl","http://192.168.1.105");

                                       localStorage.setItem("serverUrl","https://maio.capitaretail.com.cn");
                                      //localStorage.setItem("serverUrl","https://maiouat.capitaretail.com.cn");
                                       localStorage.setItem("serverUrlimoffice","https://mimoffice.capitaretail.com.cn");
                                       //setTimeout(app.login,100);
                                       getPendingListNew("1","");
                                       // init(); 
                                       },null,"UserInfo", "GetUserInfo",[]); 
                          document.addEventListener("backbutton", function(){
//                    		alert("dd");
                    		if(currentpage=="main"){
                    			//alert(currentpage);
                    			
                    			exit();
                    		}else{
                    			 window.history.back();
                    		}
                    		
                    	}, false);
                          }, false);


function search(){
    var searchvalue= document.getElementsByName("search")[0].value;
    var searchURI= encodeURIComponent(searchvalue);
    if(pageType=="Pending"){
        getPendingListNew("1",searchURI);
//        console.log("value:"+searchURI);
//        getPendingList("0",searchURI);
        
    }else if(pageType=="Complete"){
        getCompleteListNew("1",searchURI);
//        console.log("value:"+searchURI);
//        getCompleteList("0",searchURI);
    }
}
function getTime(date){
    var now= new Date();
    if(date == "0001/1/1 0:00:00"){
        return "";
    }else{
        var datestr=date.split(" ");
        var date=datestr[0].split("/");
        var year=date[0];
        var month=date[1];
        var day=date[2];
        var time=datestr[1];
        return year+"-"+month+"-"+day;
        
    }
}
function getPendingListNew(page,searchKey){
    var search=(searchKey==null)?"":searchKey;
    var adAcount=localStorage.getItem("userName");
    var serverUrl=localStorage.getItem("serverUrl");
    console.log("adAcount:"+adAcount);
    console.log("serverUrl:"+serverUrl);
    pageType="Pending";
    
    if(page!=1){
        
        $("#getPendingMoreId").remove();
        
    }
    
    
    $.mobile.showPageLoadingMsg('a', "Please wait..." );


    $.ajax({
           url:serverUrl+"/api/SearchMsgTask",
           type:"POST",
           data:{"ADAcount":adAcount,"PageNum":page,"PerNum":15,"SearchKey":search},
           dataType:"JSON",
           success:function(cl){
           console.log("result:"+JSON.stringify(cl));
           oListView=$("#listView");
           childs=[];
           if(page==1){
           pageNumber=2;
           oListView.html("");
           }
           if(cl[0].result=="True"){
           for(var i=0;i<cl[0].SearchMsgTaskList.length;i++){
           var taskEntity = cl[0].SearchMsgTaskList[i];
           console.log("taskEntity:"+JSON.stringify(taskEntity));
           var title = taskEntity.ProjectName;
           var content = taskEntity.Title;
           var bizId=taskEntity.SerialNumber;
           var Amount=taskEntity.Amount;
           var ProcStartDate=getTime(taskEntity.ReceivedTime);
           var titlename="任务详情";
           console.log("title:"+title);
           console.log("content:"+content);
           console.log("bizId:"+bizId);
           console.log("Amount:"+Amount);
           var strdata="";
           strdata=strdata+"<li><div>";
           strdata=strdata+"<a href=\"views/taskDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\">";
           strdata=strdata+"<div style=\"position:relative;\">";
           strdata=strdata+"<div style=\"float:left;font-size:16px;color:black;font-weight:bold;\">"+title+"</div>";
           strdata=strdata+"<div style=\"color:red;float:right;font-size:15px;\">"+Amount+"</div>";
           strdata=strdata+"<div style=\" clear:both;width:250px;word-wrap:break-word;color:gray;font-size:15px;\">"+content+"</div>";
           strdata=strdata+"<div style=\"float:right;color:black;font-size:12px;margin-top:-5px;color:#095b94;\">"+ProcStartDate+"</div>";
           strdata=strdata+"<img  src=\"img/arrow.png\" style=  \"right:0%;top:50%; position:absolute; width:10px; height:10px;  \"/>";
           strdata=strdata+"</div>";
           strdata=strdata+"</a>";
           strdata=strdata+"</div></li>";
           childs.push(strdata);

           
           }
           if(cl[0].SearchMsgTaskList.length>=15){
           childs.push("<li id=\"getPendingMoreId\" onclick=\"getPendingMore()\"><p style=\"text-align:center; margin-top:3px;\">更多</p></li>");
           }
           oListView.append(childs.join(""));
           oListView.listview("refresh");
           $.mobile.hidePageLoadingMsg();
           }else{
           console.log("获取数据失败！");
           $.mobile.hidePageLoadingMsg();
           }
           
           },
           error: function(xhr,textStatus,errMsg) {
          // alert(textStatus);
          navigator.notification.alert(textStatus,null,"提示","确定");
           $.mobile.hidePageLoadingMsg();
           }
           });


}

function getCompleteListNew(page,searchKey){
    var search=(searchKey==null)?"":searchKey;
    var adAcount=localStorage.getItem("userName");
    var serverUrl=localStorage.getItem("serverUrl");
    console.log("adAcount:"+adAcount);
    console.log("serverUrl:"+serverUrl);
    pageType="Complete";
    
    if(page != 1 ){
        
        $("#getCompleteMoreId").remove();
    }
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    
    $.ajax({
           url:serverUrl+"/api/SearchMsgTaskDone",
           type:"POST",
           data:{"ADAcount":adAcount,"PageNum":page,"PerNum":15,"SearchKey":search},
           dataType:"JSON",
           success:function(cl){
           console.log("result:"+JSON.stringify(cl));
           oListView=$("#listView");
           childs=[];
           if(page==1){
           pageNumber=2;
           oListView.html("");
           }
           console.log("result:"+cl[0].result);
           if(cl[0].result=="True"){
           for(var i=0;i<cl[0].SearchMsgTaskDoneList.length;i++){
           var taskEntity = cl[0].SearchMsgTaskDoneList[i];
           console.log("taskEntity:"+JSON.stringify(taskEntity));
           console.log("projectName:"+taskEntity.ProjectName);
           var title = taskEntity.ProjectName;
           var content = taskEntity.Title;
           var bizId=taskEntity.SerialNumber;
           var Amount=taskEntity.Amount;
           var ProcStartDate=getTime(taskEntity.ReceivedTime);
           var titlename="任务详情";
           console.log("title:"+title);
           console.log("content:"+content);
           console.log("bizId:"+bizId);
           console.log("Amount:"+Amount);
           var strdata="";
           strdata=strdata+"<li><div>";
           strdata=strdata+"<a href=\"views/taskCompleteDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\">";
           strdata=strdata+"<div style=\"position:relative;\">";
           strdata=strdata+"<div style=\"float:left;font-size:16px;color:black;font-weight:bold;\">"+title+"</div>";
           strdata=strdata+"<div style=\"color:red;float:right;font-size:15px;\">"+Amount+"</div>";
           strdata=strdata+"<div style=\" clear:both;width:250px;word-wrap:break-word;color:gray;font-size:15px;\">"+content+"</div>";
           strdata=strdata+"<div style=\"float:right;color:black;font-size:12px;margin-top:-5px;color:#095b94;\">"+ProcStartDate+"</div>";
           strdata=strdata+"<img  src=\"img/arrow.png\" style=  \"right:0%;top:50%; position:absolute; width:10px; height:10px;  \"/>";
           strdata=strdata+"</div>";
           strdata=strdata+"</a>";
           strdata=strdata+"</div></li>";
           childs.push(strdata);
           
           
           }
           if(cl[0].SearchMsgTaskDoneList.length>=15){
            childs.push("<li id=\"getCompleteMoreId\" onclick=\"getCompleteMore()\"><p style=\"text-align:center; margin-top:3px;font-size:15px;\">更多</p></li>");
           }
           oListView.append(childs.join(""));
           oListView.listview("refresh");
           $.mobile.hidePageLoadingMsg();
           }else{
           $.mobile.hidePageLoadingMsg();
           console.log("获取数据失败！");
           
           }
           },
           error: function(xhr,textStatus,errMsg) {
          // alert(textStatus);
          navigator.notification.alert(textStatus,null,"提示","确定");
           $.mobile.hidePageLoadingMsg();
           }
           });
    
    
}

//获取用户信息
function getUserInfo(){
  


}



//获取当前url的参数
function getPara(name){
    var url=window.location.href,paras="";
    paras=url.replace(url.match(/(.*|\w*\d)\?/gi),"");
    paras=paras.match(new RegExp(name+"=([^&])*", "gi")).toString();
    return paras.replace(new RegExp(name+"{1}=","gi"),"");
}
//获取组织结构
function getAll(){

}
function hideArea(name){
    if($("#"+name).attr("value") === "show"){
        $("."+name).hide(500,function(){$("#"+name).attr("value","hidden");});
    }
    else{
        $("."+name).show(500,function(){$("#"+name).attr("value","show");});
    }
}

function login(){
  
    var serverUrl=localStorage.getItem("serverUrlimoffice");
    SoapOp.setMethod("WS_Login");
    SoapOp.addPar("userName",localStorage.getItem("userName"));
    SoapOp.addPar("userPwd",localStorage.getItem("userPwd"));
    SoapOp.addPar("deviceId","");
    SoapOp.addPar("pushId","");
    SoapOp.addPar("osType","");
    SoapOp.addPar("osVersion","");
    SoapOp.addPar("softVersion","");
   
    Cordova.exec(function(result){
                 console.log(result);
                 $.mobile.showPageLoadingMsg('a', "Please wait..." );
                 $.ajax({
                        url:serverUrl+"/CapitalandWS/CapitalandWS.asmx",
                        type:"POST",
                        data:SoapOp.getResult(),
                        dataType:"xml",
                        beforeSend:function(xhr){
                        xhr.setRequestHeader("SOAPAction","http://tempuri.org/WS_Login");
                        xhr.setRequestHeader("Content-Type","text/xml");
                        },
                        success:function(cl){
                        $.mobile.hidePageLoadingMsg();
                        var isSuc = cl.getElementsByTagName("IsSuccess")[0].firstChild.nodeValue;
                        if(isSuc.length > 9){
                        console.log("iMofficeToken"+isSuc);
                        localStorage.setItem("iMofficeToken",isSuc);
                        getPendingList(0);
                        getPendingCount(0,1000);
                        }
                        else
                        console.log("认证失败");
                        },
                        error: function(xhr,textStatus,errMsg) {
                        console.log(textStatus+" "+xhr.status+" "+errMsg);
                        $.mobile.hidePageLoadingMsg();
                        login();
                        }
                        });
                 
                },null,"Auth", "AuthUrl",[localStorage.getItem("userName"),localStorage.getItem("userPwd"),serverUrl]);
          
}

//获取已办任务
function getCompleteList(page,searchKey){
    
    var search=(searchKey==null)?"":searchKey;
    var serverUrl=localStorage.getItem("serverUrlimoffice");
    SoapOp.setMethod("WS_GetCompleteIndex");
    SoapOp.addPar("token",localStorage.getItem("iMofficeToken"));
    SoapOp.addPar("indexPage",page);
    SoapOp.addPar("pageSize","15");
    SoapOp.addPar("searchKey",search);
    console.log("page:"+page+" search:"+search);
    
    pageType="Complete";
    
    if(page != 0 ){
        
        $("#getCompleteMoreId").remove();
    }
    
    $.mobile.showPageLoadingMsg();
    $.ajax({
           url:serverUrl+"/CapitalandWS/CapitalandWS.asmx",
           type:"POST",
           data:SoapOp.getResult(),
           dataType:"xml",
           beforeSend:function(xhr){
           xhr.setRequestHeader("SOAPAction","http://tempuri.org/WS_GetCompleteIndex");
           xhr.setRequestHeader("Content-Type","text/xml");
           },
           success:function(cl){
           
           oListView=$("#listView");
           if(page == 0 ){
                pageNumber=2;
                oListView.html("");
           }
           childs=[];
           
           var taskList = cl.getElementsByTagName("TaskEntity");
//           console.log(tasklist);
           for(var i=0;i<taskList.length;i++){

           var taskEntity = taskList[i];
           var title = taskEntity.getElementsByTagName("ProjectName")[0].firstChild.nodeValue;
           var content = taskEntity.getElementsByTagName("Title")[0].firstChild.nodeValue;
           var bizId=taskEntity.getElementsByTagName("SerialNumber")[0].firstChild.nodeValue;
           var Amount=taskEntity.getElementsByTagName("Amount")[0].firstChild.nodeValue;
           var ProcStartDate=taskEntity.getElementsByTagName("ProcStartDate")[0].firstChild.nodeValue;
           var titlename="任务详情";
           var strdata="";
               strdata=strdata+"<li><div>";
               strdata=strdata+"<a href=\"views/taskCompleteDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\">";
               strdata=strdata+"<div style=\"position:relative;\">";
               strdata=strdata+"<div style=\"float:left;font-size:16px;color:black;font-weight:bold;\">"+title+"</div>";
               strdata=strdata+"<div style=\"color:red;float:right;font-size:15px;\">"+Amount+"</div>";
           strdata=strdata+"<div style=\" clear:both;width:250px;word-wrap:break-word;color:gray;font-size:15px;\">"+content+"</div>";
               strdata=strdata+"<div style=\"float:right;color:black;font-size:12px;margin-top:-5px;color:#095b94;\">"+ProcStartDate+"</div>";
                strdata=strdata+"<img  src=\"img/arrow.png\" style=  \"right:0%;top:50%; position:absolute; width:10px; height:10px;  \"/>";
               strdata=strdata+"</div>";
               strdata=strdata+"</a>";
               strdata=strdata+"</div></li>";
           childs.push(strdata);
//           childs.push("<li class=\"area_"+i+"\" style=\"overflow:hidden;display:block;\" data-icon=\"false\"><a href=\"views/taskCompleteDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\"><div class=\"ui-listview-row-title\">"+title+"<p style=\"color:red;float:right;margin-top:3px;\">"+Amount+"</p></div><div style=\"width:300px;word-wrap:break-word;overflow:hidden; border:1px solid red;height:auto;\">"+content+"</div><div><p style=\" float:right;margin-top:3px;\">"+ProcStartDate+"</p></div></a></li>");
           
           }
           if(taskList.length>10){
                childs.push("<li id=\"getCompleteMoreId\" onclick=\"getCompleteMore()\"><p style=\"text-align:center; margin-top:3px;font-size:15px;\">更多</p></li>");
           }
           oListView.append(childs.join(""));
           oListView.listview("refresh");
           $.mobile.hidePageLoadingMsg();
           },
           error: function(xhr,textStatus,errMsg) {
           console.log(textStatus+" "+xhr.status+" "+errMsg);
           $.mobile.hidePageLoadingMsg();
           }
           });
}
//获取待办任务,做单点登录需要凯德支持
function getPendingList(page,searchKey){
    
    var search=(searchKey==null)?"":searchKey;
    
    var serverUrl=localStorage.getItem("serverUrlimoffice");
    SoapOp.setMethod("WS_GetPendingIndex");
    SoapOp.addPar("token",localStorage.getItem("iMofficeToken"));
    SoapOp.addPar("indexPage",page);
    SoapOp.addPar("pageSize","15");
    SoapOp.addPar("searchKey",search);
    pageType="Pending";
    
    if(page!=0){
        
        $("#getPendingMoreId").remove();
        
    }
    
    
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/CapitalandWS/CapitalandWS.asmx",
           type:"POST",
           data:SoapOp.getResult(),
           dataType:"xml",
           beforeSend:function(xhr){
           xhr.setRequestHeader("SOAPAction","http://tempuri.org/WS_GetPendingIndex");
           xhr.setRequestHeader("Content-Type","text/xml");
           },
           success:function(cl){
           oListView=$("#listView");
           childs=[];
           if(page==0){
           pageNumber=2;
           oListView.html("");
           }
           var taskList = cl.getElementsByTagName("TaskEntity");
//           localStorage.setItem("pendingTaskcount",taskList.length);
           for(var i=0;i<taskList.length;i++){
           var taskEntity = taskList[i];
           
           var title = taskEntity.getElementsByTagName("ProjectName")[0].firstChild.nodeValue;
           var content = taskEntity.getElementsByTagName("Title")[0].firstChild.nodeValue;
           var bizId=taskEntity.getElementsByTagName("SerialNumber")[0].firstChild.nodeValue;
           var Amount=taskEntity.getElementsByTagName("Amount")[0].firstChild.nodeValue;
           var ProcStartDate=taskEntity.getElementsByTagName("ProcStartDate")[0].firstChild.nodeValue;
           var titlename="任务详情";
           var strdata="";
           strdata=strdata+"<li><div>";
           strdata=strdata+"<a href=\"views/taskDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\">";
           strdata=strdata+"<div style=\"position:relative;\">";
           strdata=strdata+"<div style=\"float:left;font-size:16px;color:black;font-weight:bold;\">"+title+"</div>";
           strdata=strdata+"<div style=\"color:red;float:right;font-size:15px;\">"+Amount+"</div>";
           strdata=strdata+"<div style=\" clear:both;width:250px;word-wrap:break-word;color:gray;font-size:15px;\">"+content+"</div>";
           strdata=strdata+"<div style=\"float:right;color:black;font-size:12px;margin-top:-5px;color:#095b94;\">"+ProcStartDate+"</div>";
           strdata=strdata+"<img  src=\"img/arrow.png\" style=  \"right:0%;top:50%; position:absolute; width:10px; height:10px;  \"/>";
           strdata=strdata+"</div>";
           strdata=strdata+"</a>";
           strdata=strdata+"</div></li>";
           childs.push(strdata);

           //           childs.push("<li class=\"area_"+i+"\" style=\"overflow:hidden;display:block;\" data-icon=\"false\"><a href=\"views/taskDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\"><div class=\"ui-listview-row-title\">"+title+"<p style=\"color:red;float:right;margin-top:3px;\">"+Amount+"</p></div><div style=\"width:300px;word-wrap:break-word;overflow:hidden; border:1px solid red;\">"+content+"</div><div><p style=\" float:right;margin-top:3px;\">"+ProcStartDate+"</p></div></a></li>");
           
           }
           if(taskList.length>10){
           childs.push("<li id=\"getPendingMoreId\" onclick=\"getPendingMore()\"><p style=\"text-align:center; margin-top:3px;\">更多</p></li>");
           }
           oListView.append(childs.join(""));
           
           oListView.listview("refresh");
           $.mobile.hidePageLoadingMsg();
           },
           error: function(xhr,textStatus,errMsg) {
           console.log(textStatus+" "+xhr.status+" "+errMsg);
           $.mobile.hidePageLoadingMsg();
           }
           });
}
//获取待办任务数
function getPendingCount(page,PageSize){

    var serverUrl=localStorage.getItem("serverUrlimoffice");
    SoapOp.setMethod("WS_GetPendingIndex");
    SoapOp.addPar("token",localStorage.getItem("iMofficeToken"));
    SoapOp.addPar("indexPage",page);
    SoapOp.addPar("pageSize",PageSize);
    SoapOp.addPar("searchKey","");

    $.ajax({
           url:serverUrl+"/CapitalandWS/CapitalandWS.asmx",
           type:"POST",
           data:SoapOp.getResult(),
           dataType:"xml",
           beforeSend:function(xhr){
           xhr.setRequestHeader("SOAPAction","http://tempuri.org/WS_GetPendingIndex");
           xhr.setRequestHeader("Content-Type","text/xml");
           },
           success:function(cl){
           var taskList = cl.getElementsByTagName("TaskEntity");
           console.log("taskCount:"+taskList.length);
           localStorage.setItem("pendingTaskcount",taskList.length);
           },
           error: function(xhr,textStatus,errMsg) {
           console.log(textStatus+" "+xhr.status+" "+errMsg);
           }
           });
}

function getCompleteMore(){
    
    getCompleteListNew(pageNumber);
    pageNumber=pageNumber+1;
    console.log("pageNumber="+pageNumber);
    
}
function getPendingMore(){
    getPendingListNew(pageNumber);
    pageNumber=pageNumber+1;
    console.log("pageNumber="+pageNumber);
}

var SoapOp={
result:"",
method:"",
setMethod:function(methodName){
    method = methodName;
    result = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    result += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns=\"http://tempuri.org\"><soap:Body><"+method+" xmlns=\"http://tempuri.org/\">";
},
addPar:function(parName,parValue){
    result += "<"+parName+">"+parValue+"</"+parName+">";
},
getResult:function(){
    result += "</"+method+"></soap:Body></soap:Envelope>";
    return result;
}
    
};


window.app={
getCompleteList:getCompleteList,
getPendingList:getPendingList,
getPara:getPara,
getAll:getAll,
login:login
};