var pageNumber,
pageType,
currentpage,
UnreadNoticeCount,
isCN;

$(document).bind("mobileinit",function(){
                 $.mobile.ajaxEnabled=true;
                 //$.mobile.loadingMessageTextVisible = true;
                 }
                 );
$(document).bind("pageshow",function(e,ui){     
				currentpage=e.target.id;
				
                 switch(e.target.id){
                 case "main":
                 //刚打开app时才调用
                 if($("#listView>li").length===0){
//                    setTimeout(getUserInfo,100);
                	 localStorage.setItem("UnreadNoticeCount",-1);
                 }else{
                	 
                 getUnreadNoticeCount(0,1000);
                  
                 if(pageType=="Notice"){
                 
                 getNoticeListNew("1");
                 
                 }else if(pageType=="UnreadNotice"){
                 
                 getUnreadNoticeListNew("1");
                 }
//                 getUnreadNoticeCount(0);
                 
                 }
                    break;
                 case "depts":
                    setDeptList(app.getPara("deptList"));
                    break;
                 case "noticeDetails":
                 {

                 getnotice(app.getPara("SerialNumber"));

                 }
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
                 case "noticeDetails":
                 {
        
                   $("#id-noticeDetails-title").text(unescape(decodeURIComponent(app.getPara("name"))));
                 
                 }
                 break;
                 }
                 });
$(document).bind("pagebeforechange",function(){
                 //$.mobile.showPageLoadingMsg('a', "Please wait...");
                 });

function search(){
    var searchvalue= document.getElementsByName("search")[0].value;
    var searchURI= encodeURIComponent(searchvalue);
    if(pageType=="Notice"){
        console.log("Notice");
        console.log("value:"+searchURI);
//        getNoticeList("0",searchURI);
        getNoticeListNew("1",searchURI);
        
    }else if(pageType=="UnreadNotice"){
        console.log("unreadNotice");
        console.log("value:"+searchURI);
//        getUnreadNoticeList("0",searchURI);
        getUnreadNoticeListNew("1",searchURI);
    }
}
//返回到cmamovile主界面
function exit(){
   
	
    Cordova.exec(function(result){
                 //                 localStorage.removeItem("UnreadNoticeCount");
                 },null,"Application", "exit",[localStorage.getItem("UnreadNoticeCount"),"-1","NO",localStorage.getItem("AppID")]);
}
document.addEventListener("deviceready", function(){
                          Cordova.exec(function(result){
                                       localStorage.setItem("AppID",result.appID);
                                       localStorage.setItem("userToken",result.userToken);
                                       localStorage.setItem("userName",result.userName );
                                       localStorage.setItem("userPwd",result.userPassword);
                                       localStorage.setItem("language",result.language);
                                       //localStorage.setItem("serverUrlimoffice","http://10.154.128.13");
//                                       localStorage.setItem("serverUrl","http://192.168.1.105");
                                       isCN = result.language == "中文"?true:false;
                                       if(result.userName.substring(0, 2)=="w_" ||result.userName.substring(0, 2)=="W_"){
                                           navigator.notification.alert(isCN?"无权限访问！":"No permission to access!",exit,isCN?"提示":"Prompt",isCN?"确定":"OK");
                                           
                                           }
                                       localStorage.setItem("serverUrl","https://maio.capitaretail.com.cn");
                                       //  localStorage.setItem("serverUrl","https://maiouat.capitaretail.com.cn");
                                       localStorage.setItem("serverUrlimoffice","https://mimoffice.capitaretail.com.cn");
//                                       setTimeout(app.login,100);
                                       getUnreadNoticeListNew("1");
                                       // init();
                                       },null,"UserInfo", "GetUserInfo",[]);
                          document.addEventListener("backbutton", function(){
//                        		alert("dd");
                        		if(currentpage=="main"){
                        			//alert(currentpage);
                        			
                        			exit();
                        		}else{
                        			
                        			 window.history.back();
                        		}
                        		
                        	}, false); 
                          
                          }, false);


//获取用户信息
//function getUserInfo(){
//  
//    Cordova.exec(getUserInfoSuc,null,"UserInfo", "GetUserInfo",[]);
////    Cordova.exec(null,null,"UserInfo", "WriteItellDrafts",[value:"dasdfasdfasdf",url:"sdfagasgasfsdf",content:"dfasdfasdfasdfa",key:"sdsafasdf" ]);
//    
//}
//function getUserInfoSuc(result){
//   
//    localStorage.setItem("AppID",result.appID);
//    localStorage.setItem("userToken",result.userToken);
//    localStorage.setItem("userName",result.userName );
//    localStorage.setItem("userPwd",result.userPassword);
//    localStorage.setItem("language",result.language);
//    localStorage.setItem("serverUrl","https://imoffice.capitaretail.com.cn"); //凯德生产服务器
//// localStorage.setItem("serverUrl","http://10.154.128.13");//凯德UAT服务器
//    setTimeout(app.login,100);
////    var tokenkey= result.userToken;
//    //getAreaAndMarket(tokenkey);
//    
//}
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

function getNoticeListNew(page,searchKey){
    var search=(searchKey==null)?"":searchKey;
    var adAcount=localStorage.getItem("userName");
    var serverUrl=localStorage.getItem("serverUrl");
    console.log("adAcount"+adAcount);
    console.log("serverUrl"+serverUrl);
    pageType="Notice";
    
    if(page != 0 ){
        
        $("#getNoticeMoreId").remove();
    }
    
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/api/SearchMsgNoticeDone",
           type:"POST",
           data:{"ADAcount":adAcount,"PageNum":page,"PerNum":15,"SearchKey":search},
           dataType:"JSON",
           success:function(cl){
           oListView=$("#listView");
           childs=[];
           if(page == 1){
           pageNumber=2;
           oListView.html("");
           }
           console.log("cl:"+cl);
           console.log("result："+JSON.stringify(cl[0]));
           if(cl[0].result=="True"){
           for(var i=0;i<cl[0].SearchMsgNoticeDoneList.length;i++){
           var noticeEntity = cl[0].SearchMsgNoticeDoneList[i];
           console.log("noticeEntity:"+JSON.stringify(noticeEntity));
           var title = noticeEntity.Description;
           var content = noticeEntity.Title;
           var bizId=noticeEntity.BizID;
           var SendTime=getTime(noticeEntity.ReceivedTime);
           var titlename="NoticeDetails";
           var strdata="";
           strdata=strdata+"<li><div>";
           strdata=strdata+"<a href=\"views/noticeDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\">";
           strdata=strdata+"<div style=\"position:relative;\">";
           strdata=strdata+"<div style=\"float:left;font-size:16px;color:black;font-weight:bold;\">"+title+"</div>";
           
           strdata=strdata+"<div style=\" clear:both;width:250px;word-wrap:break-word;color:gray;font-size:15px;\">"+content+"</div>";
           strdata=strdata+"<div style=\"float:right;color:black;font-size:12px;margin-top:-5px;color:#095b94;\">"+SendTime+"</div>";
           strdata=strdata+"<img  src=\"img/arrow.png\" style=  \"right:5%;top:50%; position:absolute; width:10px; height:10px;  \"/>";
           strdata=strdata+"</div>";
           strdata=strdata+"</a>";
           strdata=strdata+"</div></li>";
           childs.push(strdata);
          
           
           
           }
           if(cl[0].SearchMsgNoticeDoneList.length>=15){
            childs.push("<li id=\"getNoticeMoreId\" onclick=\"getNoticeMore()\"><p style=\"text-align:center; margin-top:3px;font-size:15px;\">更多</p></li>");
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
//           alert(textStatus);
			navigator.notification.alert(textStatus,null,"提示","确定");
           console.log("textStatus:"+textStatus);
           console.log("errmsg:"+errMsg);
           $.mobile.hidePageLoadingMsg();
           }
           });



}

function getUnreadNoticeListNew(page,searchKey){
    var search=(searchKey==null)?"":searchKey;
    var adAcount=localStorage.getItem("userName");
    var serverUrl=localStorage.getItem("serverUrl");
    console.log("adAcount"+adAcount);
    console.log("serverUrl"+serverUrl);
    pageType="UnreadNotice";
    
    if(page != 0 ){
        
        $("#getUnreadNoticeMoreId").remove();
    }
    
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/api/SearchMsgNotice",
           type:"POST",
           data:{"ADAcount":adAcount,"PageNum":page,"PerNum":15,"SearchKey":search},
           dataType:"JSON",
           success:function(cl){
           oListView=$("#listView");
           childs=[];
           if(page == 1){
           pageNumber=2;
           oListView.html("");
           }
         
           console.log("result："+JSON.stringify(cl));
           if(cl[0].result=="True"){
           for(var i=0;i<cl[0].SearchMsgNoticeList.length;i++){
           var noticeEntity = cl[0].SearchMsgNoticeList[i];
           console.log("noticeEntity:"+JSON.stringify(noticeEntity));
           var title = noticeEntity.Description;
           var content = noticeEntity.Title;
           var bizId=noticeEntity.BizID;
           var SendTime=getTime(noticeEntity.ReceivedTime);
           var titlename="NoticeDetails";
           var strdata="";
           strdata=strdata+"<li><div>";
           strdata=strdata+"<a href=\"views/noticeDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\">";
           strdata=strdata+"<div style=\"position:relative;\">";
           strdata=strdata+"<div style=\"float:left;font-size:16px;color:black;font-weight:bold;\">"+title+"</div>";
           
           strdata=strdata+"<div style=\" clear:both;width:250px;word-wrap:break-word;color:gray;font-size:15px;\">"+content+"</div>";
           strdata=strdata+"<div style=\"float:right;color:black;font-size:12px;margin-top:-5px;color:#095b94;\">"+SendTime+"</div>";
           strdata=strdata+"<img  src=\"img/arrow.png\" style=  \"right:5%;top:50%; position:absolute; width:10px; height:10px;  \"/>";
           strdata=strdata+"</div>";
           strdata=strdata+"</a>";
           strdata=strdata+"</div></li>";
           childs.push(strdata);
           
           
           
           }
           if(cl[0].SearchMsgNoticeList.length>=15){
           childs.push("<li id=\"getNoticeMoreId\" onclick=\"getNoticeMore()\"><p style=\"text-align:center; margin-top:3px;font-size:15px;\">更多</p></li>");
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
           //           alert(textStatus);
           navigator.notification.alert(textStatus,null,"提示","确定");
           console.log("textStatus:"+textStatus);
           console.log("errmsg:"+errMsg);
           $.mobile.hidePageLoadingMsg();
           }
           });
    
    
    
}

//获取当前url的参数
function getPara(name){
    var url=window.location.href,paras="";
    paras=url.replace(url.match(/(.*|\w*\d)\?/gi),"");
    paras=paras.match(new RegExp(name+"=([^&])*", "gi")).toString();
    return paras.replace(new RegExp(name+"{1}=","gi"),"");
}


function login(){
    var serverUrl=localStorage.getItem("serverUrlimoffice");
    SoapOp.setMethod("WS_Login");
//    SoapOp.addPar("userName","apptestuser03");
//    SoapOp.addPar("userPwd","p@ssword123");
    SoapOp.addPar("userName",localStorage.getItem("userName"));
    SoapOp.addPar("userPwd",localStorage.getItem("userPwd"));
    SoapOp.addPar("deviceId","");
    SoapOp.addPar("pushId","");
    SoapOp.addPar("osType","");
    SoapOp.addPar("osVersion","");
    SoapOp.addPar("softVersion","");
    Cordova.exec(function(result){
                
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
                        localStorage.setItem("iMofficeToken",isSuc);
                        console.log("token:"+isSuc);
                        getUnreadNoticeList(0);
                        getUnreadNoticeCount(0,1000);
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
//获取已读通知
function getNoticeList(page,searchKey){
    var search=(searchKey==null)?"":searchKey;
    console.log("searchvalue:"+search);
    var serverUrl=localStorage.getItem("serverUrlimoffice");
    SoapOp.setMethod("WS_GetNoticeIndex");
    SoapOp.addPar("token",localStorage.getItem("iMofficeToken"));
    SoapOp.addPar("indexPage",page);
    SoapOp.addPar("pageSize","15");
    SoapOp.addPar("searchKey",search);
    pageType="Notice";
    
    if(page != 0 ){
        
        $("#getNoticeMoreId").remove();
    }
    
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/CapitalandWS/CapitalandWS.asmx",
           type:"POST",
           data:SoapOp.getResult(),
           dataType:"xml",
           beforeSend:function(xhr){
           xhr.setRequestHeader("SOAPAction","http://tempuri.org/WS_GetNoticeIndex");
           xhr.setRequestHeader("Content-Type","text/xml");
           },
           success:function(cl){
           oListView=$("#listView");
           childs=[];
           if(page == 0){
           pageNumber=2;
           oListView.html("");
           }
           var noticeList = cl.getElementsByTagName("NoticeEntity");
           for(var i=0;i<noticeList.length;i++){
          
           var noticeEntity = noticeList[i];
           var title = noticeEntity.getElementsByTagName("Title")[0].firstChild.nodeValue;
           var content = noticeEntity.getElementsByTagName("Description")[0].firstChild.nodeValue;
           var bizId=noticeEntity.getElementsByTagName("BizID")[0].firstChild.nodeValue;
           var SendTime=noticeEntity.getElementsByTagName("SendTime")[0].firstChild.nodeValue;
           var titlename="NoticeDetails";
           var strdata="";
           strdata=strdata+"<li><div>";
           strdata=strdata+"<a href=\"views/noticeDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\">";
           strdata=strdata+"<div style=\"\">";
           strdata=strdata+"<div style=\"float:left;font-size:16px;color:black;font-weight:bold;\">"+title+"</div>";

           strdata=strdata+"<div style=\" clear:both;width:250px;word-wrap:break-word;color:gray;font-size:15px;\">"+content+"</div>";
           strdata=strdata+"<div style=\"float:right;color:black;font-size:12px;margin-top:-5px;color:#095b94;\">"+SendTime+"</div>";
           strdata=strdata+"<img  src=\"img/arrow.png\" style=  \"right:5%;top:50%; position:absolute; width:10px; height:10px;  \"/>";
           strdata=strdata+"</div>";
           strdata=strdata+"</a>";
           strdata=strdata+"</div></li>";
           childs.push(strdata);
//           childs.push("<li class=\"area_"+i+"\" style=\"overflow:hidden;display:block;\" data-icon=\"false\"><a href=\"views/noticeDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\"><div class=\"ui-listview-row-title\">"+title+"</div><div class=\"ui-listview-row-content\">"+content+"</div></a></li>");
           
           }
           if(noticeList.length>10){
           childs.push("<li id=\"getNoticeMoreId\" onclick=\"getNoticeMore()\"><p style=\"text-align:center; margin-top:3px;font-size:15px;\">更多</p></li>");
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

function pushDetailInfo(object)
{

    //第一步获取id
    //根据id和url从服务器获取详情
    //展现详情
}
//获取未读通知，凯德服务器
function getUnreadNoticeCount(page,pageSize){
	
    var serverUrl=localStorage.getItem("serverUrlimoffice");
    SoapOp.setMethod("WS_GetUnreadNoticeIndex");
    SoapOp.addPar("token",localStorage.getItem("iMofficeToken"));
    SoapOp.addPar("indexPage",page);
    SoapOp.addPar("pageSize",pageSize);
    SoapOp.addPar("searchKey","");
    
    
//    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/CapitalandWS/CapitalandWS.asmx",
           type:"POST",
           data:SoapOp.getResult(),
           dataType:"xml",
           beforeSend:function(xhr){
           xhr.setRequestHeader("SOAPAction","http://tempuri.org/WS_GetUnreadNoticeIndex");
           xhr.setRequestHeader("Content-Type","text/xml");
           },
           success:function(cl){
           var noticeList = cl.getElementsByTagName("NoticeEntity");
           console.log("unread:"+noticeList.length);
           UnreadNoticeCount=noticeList.length;
          
           localStorage.setItem("UnreadNoticeCount",noticeList.length);
           },
           error: function(xhr,textStatus,errMsg) {
           console.log(textStatus+" "+xhr.status+" "+errMsg);
//           $.mobile.hidePageLoadingMsg();
           }
           });
}
//获取未读通知，凯德服务器
function getUnreadNoticeList(page,searchKey){
    var search=(searchKey==null)?"":searchKey;
    console.log("searchvalue:"+search);
    var serverUrl=localStorage.getItem("serverUrlimoffice");
    SoapOp.setMethod("WS_GetUnreadNoticeIndex");
    SoapOp.addPar("token",localStorage.getItem("iMofficeToken"));
    SoapOp.addPar("indexPage",page);
    SoapOp.addPar("pageSize","15");
    SoapOp.addPar("searchKey",search);
    pageType="UnreadNotice";
    
    if(page != 0 ){
        
        $("#getUnreadNoticeMoreId").remove();
    }
    
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/CapitalandWS/CapitalandWS.asmx",
           type:"POST",
           data:SoapOp.getResult(),
           dataType:"xml",
           beforeSend:function(xhr){
           xhr.setRequestHeader("SOAPAction","http://tempuri.org/WS_GetUnreadNoticeIndex");
           xhr.setRequestHeader("Content-Type","text/xml");
           },
           success:function(cl){
           
           oListView=$("#listView");
           if(page == 0){
           pageNumber=2;
           oListView.html("");
           }
           childs=[];
           
           var noticeList = cl.getElementsByTagName("NoticeEntity");
           console.log("unread:"+noticeList.length);
//           localStorage.setItem("UnreadNoticeCount",noticeList.length);
           for(var i=0;i<noticeList.length;i++){
           var noticeEntity = noticeList[i];
           var title = noticeEntity.getElementsByTagName("Title")[0].firstChild.nodeValue;
           var content = noticeEntity.getElementsByTagName("Description")[0].firstChild.nodeValue;
           var bizId=noticeEntity.getElementsByTagName("BizID")[0].firstChild.nodeValue;
           var referID=noticeEntity.getElementsByTagName("ReferID")[0].firstChild.nodeValue;
           var SendTime=noticeEntity.getElementsByTagName("SendTime")[0].firstChild.nodeValue;
           var titlename="NoticeDetails";
           var strdata="";
           strdata=strdata+"<li><div>";
           strdata=strdata+"<a href=\"views/noticeDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\">";
           strdata=strdata+"<div style=\"\">";
           strdata=strdata+"<div style=\"float:left;font-size:16px;color:black;font-weight:bold;\">"+title+"</div>";
           
           strdata=strdata+"<div style=\" clear:both;width:250px;word-wrap:break-word;color:gray;font-size:15px;\">"+content+"</div>";
           strdata=strdata+"<div style=\"float:right;color:black;font-size:12px;margin-top:-5px;color:#095b94;\">"+SendTime+"</div>";
           strdata=strdata+"<img  src=\"img/arrow.png\" style=  \"right:5%;top:50%; position:absolute; width:10px; height:10px;  \"/>";
           strdata=strdata+"</div>";
           strdata=strdata+"</a>";
           strdata=strdata+"</div></li>";
           childs.push(strdata);
//           childs.push("<li class=\"area_"+i+"\" style=\"overflow:hidden;display:block;\" data-icon=\"false\"><a href=\"views/noticeDetails.html?SerialNumber="+bizId+"&name="+titlename+"\" data-transition=\"slide\"><div class=\"ui-listview-row-title\">"+title+"</div><div class=\"ui-listview-row-content\">"+content+"</div></a></li>");
           
           }
           if(noticeList.length>10){
           childs.push("<li id=\"getUnreadNoticeMoreId\" onclick=\"getUnreadNoticeMore()\"><p style=\"text-align:center; margin-top:3px;font-size:15px;\">更多</p></li>");
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

function readAll(){
    console.log("in readAll");
    var serverUrl=localStorage.getItem("serverUrl");
    var tokenkey=window.localStorage.getItem("userToken");
    var adAcount=window.localStorage.getItem("userName");
    var appId=window.localStorage.getItem("AppID");
    var os=window.localStorage.getItem("OS");
    
   $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/api/UserMinusBadge",
           type:"POST",
           data:{"tokenkey":tokenkey,"ADAcount":adAcount,"AppId":appId,"Token":"","OS":os,"OSVer":"","BadgeNum":"0"},
           dataType:"html",
           success:function(cl){
        	localStorage.setItem("UnreadNoticeCount","0");
           console.log(cl);
           if(pageType=="Notice"){
           
           getNoticeList("0");
           
           }else if(pageType=="UnreadNotice"){
           
           getUnreadNoticeList("0");
           }
           
           $.mobile.hidePageLoadingMsg();
           
           },
           error: function(xhr,textStatus,errMsg) {
//           alert(textStatus);
           $.mobile.hidePageLoadingMsg();
           }
           });

}
function getNoticeMore(){
    
    getNoticeListNew(pageNumber);
    pageNumber=pageNumber+1;
    console.log("pageNumber="+pageNumber);
    
}
function getUnreadNoticeMore(){
    getUnreadNoticeListNew(pageNumber);
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
    getPara:getPara,
    getNoticeList:getNoticeList,
    getUnreadNoticeList:getUnreadNoticeList,
    login:login
};