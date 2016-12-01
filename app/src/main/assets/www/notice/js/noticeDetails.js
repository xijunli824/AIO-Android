var SerialNumberStr;

function getnotice(SerialNumber){
    
    
    document.getElementsByName("serialNumber")[0].value=SerialNumber;
    
    getnoticeDetail(SerialNumber);
    
    
}
function getDetail(){
    
    SerialNumberStr=document.getElementsByName("serialNumber")[0].value;
    
    getnoticeDetail(SerialNumberStr);
    
    
}
function getSuggestion(){
    
    
    SerialNumberStr=document.getElementsByName("serialNumber")[0].value;
    
    
    getnoticeSuggestion(SerialNumberStr);
    
    
}
function getAccessory(){
    SerialNumberStr=document.getElementsByName("serialNumber")[0].value;
    
    getnoticeAccessory(SerialNumberStr)
    
    
}
//$("#noticeDetail").onclick=function(){
//    alert(SerialNumberStr);
//    getnoticeSuggestion(SerialNumberStr);
//};
//$("#noticeSuggestion").onclick=function(){
//    alert(SerialNumberStr);
//    getnoticeSuggestion(SerialNumberStr);
//};
//$("#noticeAccessory").onclick=function(){
//    alert(SerialNumberStr);
//    getnoticeAccessory(SerialNumberStr);
//};
function getnoticeDetail(SerialNumber){
   
    var serverUrl=localStorage.getItem("serverUrl");
    var serverUrlimoffice=localStorage.getItem("serverUrlimoffice");
    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");
    var tokenkey=localStorage.getItem("userToken");
    
    
   //	var urlstr=serverUrlimoffice+"/iPhonePortal/BasicInfo.aspx?SerialNumber="+SerialNumber;	//uat
    var urlstr=serverUrlimoffice+"/iPhoneWebPortal/BasicInfo.aspx?SerialNumber="+SerialNumber;				//product
    
    console.log(urlstr);
    
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/api/GetContextByUrl",
           type:"POST",
           data:{"tokenkey":tokenkey,"url":urlstr,"userid":username,"password":password},
           dataType:"html",
           success:function(cl){
        	   var num=localStorage.getItem("UnreadNoticeCount");
               //  console.log("dddddd"+num);
                 localStorage.setItem("UnreadNoticeCount",parseInt(num)-1);
           console.log(cl)
           $.mobile.hidePageLoadingMsg();
           $("#id-noticeDetails-content").html(cl);

           
           
           },
           error: function(xhr,textStatus,errMsg) {
          // alert(textStatus);
          navigator.notification.alert(textStatus,null,"提示","确定");
           $.mobile.hidePageLoadingMsg();
           }
           });

    
}
function getnoticeSuggestion(SerialNumber){
    var serverUrl=localStorage.getItem("serverUrl");
    var serverUrlimoffice=localStorage.getItem("serverUrlimoffice");
    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");
    var tokenkey=localStorage.getItem("userToken");
    
	 // var urlstr=serverUrlimoffice+"/iPhonePortal/AuditList.aspx?SerialNumber="+SerialNumber;	//uat
    var urlstr=serverUrlimoffice+"/iPhoneWebPortal/AuditList.aspx?SerialNumber="+SerialNumber;				//product
    
    $.mobile.showPageLoadingMsg('a', "Please wait..." );

    console.log(urlstr);

    $.ajax({
           url:serverUrl+"/api/GetContextByUrl",
           type:"POST",
           data:{"tokenkey":tokenkey,"url":urlstr,"userid":username,"password":password},
           dataType:"html",
           success:function(cl){
           console.log(cl)
           $.mobile.hidePageLoadingMsg();
           $("#id-noticeDetails-content").html(cl);
           
           
           
           },
           error: function(xhr,textStatus,errMsg) {
           alert(textStatus);
           $.mobile.hidePageLoadingMsg();
           }
           });
    
}
function getnoticeAccessory(SerialNmber){ 
    
    var serverUrl=localStorage.getItem("serverUrl");
    var serverUrlimoffice=localStorage.getItem("serverUrlimoffice");
    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");
    var tokenkey=localStorage.getItem("userToken");
    
    //var urlstr=serverUrlimoffice+"/iPhonePortal/Attachment.aspx?SerialNumber="+SerialNumber;	//uat

    var urlstr=serverUrlimoffice+"/iPhoneWebPortal/Attachment.aspx?SerialNumber="+SerialNmber;         //product
    console.log(urlstr);
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/api/GetContextByUrl",
           type:"POST",
           data:{"tokenkey":tokenkey,"url":urlstr,"userid":username,"password":password},
           dataType:"html",
           success:function(cl){
           console.log(cl)
           $.mobile.hidePageLoadingMsg();
           $("#id-noticeDetails-content").html(cl);
           
           
           
           },
           error: function(xhr,textStatus,errMsg) {
          // alert(textStatus);
          navigator.notification.alert(textStatus,null,"提示","确定");
           $.mobile.hidePageLoadingMsg();
           }
           });
    
}
function createobj() {
    if (window.ActiveXObject) {
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
    else if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
    }
}
