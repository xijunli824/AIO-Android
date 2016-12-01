
var SerialNumberStr;

function getTask(SerialNumber){
    
    
    document.getElementsByName("serialNumber").value=SerialNumber;
    //getnoticeDetail(SerialNumber);
    getTaskDetail(SerialNumber);

    
}
function getDetail(){
//    alert("aa");
    SerialNumberStr=document.getElementsByName("serialNumber").value;
    
//    getnoticeDetail(SerialNumberStr);
    getTaskDetail(SerialNumberStr);
    
    
}
function getSuggestion(){
    
//    alert("bb");
    SerialNumberStr=document.getElementsByName("serialNumber").value;
    //getnoticeSuggestion(SerialNumberStr);
    getTaskSuggestion(SerialNumberStr);
    
}
function getAccessory(){
//    alert("cc");
    SerialNumberStr=document.getElementsByName("serialNumber").value;
    
//    getnoticeAccessory(SerialNumberStr);
    getTaskAccessory(SerialNumberStr);
    
}
function getApproval(){
  
    SerialNumberStr=document.getElementsByName("serialNumber").value;
    

    getTaskApproval(SerialNumberStr);
    
}

function getTaskDetail(SerialNumber){
    var serverUrl=localStorage.getItem("serverUrl");
    var serverUrlimoffice=localStorage.getItem("serverUrlimoffice");
    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");
    var tokenkey=localStorage.getItem("userToken");
//    var urlstr=serverUrlimoffice+"/iPhonePortal/BasicInfo.aspx?SerialNumber="+SerialNumber;//UAT
    var urlstr=serverUrlimoffice+"/iPhoneWebPortal/BasicInfo.aspx?SerialNumber="+SerialNumber;//生产
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
               setTimeout($("#id-task-content").html(cl),200);
//               $("#id-task-content").html(cl);
               document.getElementById("ApprovalControl_BtnConfirm").onclick=BtnConfirm;
               document.getElementById("ApprovalControl_BtnCancel").onclick=BtnCancel;
    
               
               },
               error: function(xhr,textStatus,errMsg) {
              // alert(textStatus);
              navigator.notification.alert(textStatus,null,"提示","确定");
               $.mobile.hidePageLoadingMsg();
               }
               });
    
}
function getTaskSuggestion(SerialNumber){
  
    var serverUrl=localStorage.getItem("serverUrl");
    var serverUrlimoffice=localStorage.getItem("serverUrlimoffice");
    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");
    var tokenkey=localStorage.getItem("userToken");
//    var urlstr=serverUrlimoffice+"/iPhonePortal/AuditList.aspx?SerialNumber="+SerialNumber;//UAT
    var urlstr=serverUrlimoffice+"/iPhoneWebPortal/AuditList.aspx?SerialNumber="+SerialNumber;//生产
    console.log(urlstr);
   $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/api/GetContextByUrl",
           type:"POST",
           data:{"tokenkey":tokenkey,"url":urlstr,"userid":username,"password":password},
           dataType:"html",
           success:function(cl){
           console.log(cl)
           
           $("#id-task-content").html(cl);
           
           $.mobile.hidePageLoadingMsg();
           },
           error: function(xhr,textStatus,errMsg) {
           //alert(textStatus);
           navigator.notification.alert(textStatus,null,"提示","确定");
           $.mobile.hidePageLoadingMsg();
           }
           });

}
function getTaskAccessory(SerialNumber){
     
    var serverUrl=localStorage.getItem("serverUrl");
    var serverUrlimoffice=localStorage.getItem("serverUrlimoffice");
    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");
    var tokenkey=localStorage.getItem("userToken");
//    var urlstr=serverUrlimoffice+"/iPhonePortal/Attachment.aspx?SerialNumber="+SerialNumber;//UAT
    var urlstr=serverUrlimoffice+"/iPhoneWebPortal/Attachment.aspx?SerialNumber="+SerialNumber;//生产
    console.log(urlstr);
$.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/api/GetContextByUrl",
           type:"POST",
           data:{"tokenkey":tokenkey,"url":urlstr,"userid":username,"password":password},
           dataType:"html",
           success:function(cl){
           console.log(cl)
           $("#id-task-content").html(cl);
           
           $.mobile.hidePageLoadingMsg();
           },
           error: function(xhr,textStatus,errMsg) {
           //alert(textStatus);
           navigator.notification.alert(textStatus,null,"提示","确定");
           $.mobile.hidePageLoadingMsg();
           }
           });
    
}
function getTaskApproval(SerialNumber){
    
    var serverUrl=localStorage.getItem("serverUrl");
    var serverUrlimoffice=localStorage.getItem("serverUrlimoffice");
    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");
    var tokenkey=localStorage.getItem("userToken");
//    var urlstr=serverUrlimoffice+"/iPhonePortal/Approval.aspx?SerialNumber="+SerialNumber;//UAT
    var urlstr=serverUrlimoffice+"/iPhoneWebPortal/Approval.aspx?SerialNumber="+SerialNumber;//生产
    console.log(urlstr);
$.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:serverUrl+"/api/GetContextByUrl",
           type:"POST",
           data:{"tokenkey":tokenkey,"url":urlstr,"userid":username,"password":password},
           dataType:"html",
           success:function(cl){
           $.mobile.hidePageLoadingMsg();
           console.log(cl)
           setTimeout($("#id-task-content").html(cl),200);
           
           document.getElementById("ApprovalControl_BtnConfirm").onclick=BtnConfirm1;
           document.getElementById("ApprovalControl_BtnCancel").onclick=BtnCancel1;
           
           
           },
           error: function(xhr,textStatus,errMsg) {
           //alert(textStatus);
           navigator.notification.alert(textStatus,null,"提示","确定");
           $.mobile.hidePageLoadingMsg();
           }
           });
    
}



function BtnConfirm(){

    var SerialNumber=document.getElementsByName("serialNumber").value;
    var serverUrl=localStorage.getItem("serverUrl");
    var username=localStorage.getItem("userName");
    
    var MessageText=document.getElementById("ApprovalControl_TextAreaComment").value;


    console.log(MessageText);
    console.log(SerialNumber);
    if (confirm("确认同意？")){
        console.log("in BtnConfirm");
        $.ajax({
               url:serverUrl+"/api/Approval",
               type:"POST",
               data:{"UserAccount":username,"SerialNumber":SerialNumber,"MessageText":MessageText,"iPhoneApprovalActionType":"1"},
               dataType:"json",
               success:function(cl){
               $.mobile.hidePageLoadingMsg();
               console.log(cl[0].result);
               if(cl[0].result=="True"){
               var num=localStorage.getItem("pendingTaskcount");
               localStorage.setItem("pendingTaskcount",parseInt(num)-1);
               console.log(cl);
              // alert("提交成功！");
               navigator.notification.alert("提交成功！",null,"提示","确定");
               window.history.back();
//               getTaskDetail(SerialNumber);
               }else{
              // alert(cl[0].Message);
              navigator.notification.alert(cl[0].Message,null,"提示","确定");
               }
//               setTimeout($("#id-task-content").html(cl),200);
//               
//               document.getElementById("ApprovalControl_BtnConfirm").onclick=BtnConfirm;
//               document.getElementById("ApprovalControl_BtnCancel").onclick=BtnCancel;
               
               
               },
               error: function(xhr,textStatus,errMsg) {
               //alert(textStatus);
                navigator.notification.alert(textStatus,null,"提示","确定");
               $.mobile.hidePageLoadingMsg();
               }
               });
        
    }
    
}
function BtnCancel(){
//    console.log("========"+url);
    var SerialNumber=document.getElementsByName("serialNumber").value;
    var serverUrl=localStorage.getItem("serverUrl");
    var username=localStorage.getItem("userName");
    
     var MessageText=document.getElementById("ApprovalControl_TextAreaComment").value;
    console.log(MessageText);
    console.log(SerialNumber);
    if (confirm("确认拒绝？")){
        console.log("in btncancel");
        $.ajax({
               url:serverUrl+"/api/Approval",
               type:"POST",
               data:{"UserAccount":username,"SerialNumber":SerialNumber,"MessageText":MessageText,"iPhoneApprovalActionType":"2"},
               dataType:"json",
               success:function(cl){
               $.mobile.hidePageLoadingMsg();
               console.log(cl[0].result);
               if(cl[0].result=="True"){
               var num=localStorage.getItem("pendingTaskcount");
               localStorage.setItem("pendingTaskcount",parseInt(num)-1);
               console.log(cl);
              // alert("提交成功！");
              navigator.notification.alert("提交成功！",null,"提示","确定");
               window.history.back();
//               getTaskDetail(SerialNumber);
               }else{
              // alert(cl[0].Message);
               navigator.notification.alert(cl[0].Message,null,"提示","确定");
               }
//               setTimeout($("#id-task-content").html(cl),200);
//               
//               document.getElementById("ApprovalControl_BtnConfirm").onclick=BtnConfirm;
//               document.getElementById("ApprovalControl_BtnCancel").onclick=BtnCancel;
               
               
               },
               error: function(xhr,textStatus,errMsg) {
              // alert(textStatus);
              navigator.notification.alert(textStatus,null,"提示","确定");
               $.mobile.hidePageLoadingMsg();
               }
               });
        
    
    
    }
}
function BtnConfirm1(){
    
    var SerialNumber=document.getElementsByName("serialNumber").value;
    var serverUrl=localStorage.getItem("serverUrl");
    var username=localStorage.getItem("userName");
    
    var MessageText=document.getElementById("ApprovalControl_TextAreaComment").value;
    
    
    console.log(MessageText);
    console.log(SerialNumber);
    if (confirm("确认同意？")){
        console.log("in BtnConfirm");
        $.ajax({
               url:serverUrl+"/api/Approval",
               type:"POST",
               data:{"UserAccount":username,"SerialNumber":SerialNumber,"MessageText":MessageText,"iPhoneApprovalActionType":"1"},
               dataType:"json",
               success:function(cl){
               $.mobile.hidePageLoadingMsg();
               console.log(cl[0].result);
               if(cl[0].result=="True"){
               var num=localStorage.getItem("pendingTaskcount");
               localStorage.setItem("pendingTaskcount",parseInt(num)-1);
               console.log(cl);
              // alert("提交成功！");
              navigator.notification.alert("提交成功！",null,"提示","确定");
               window.history.back();
//               getTaskApproval(SerialNumber);
               }else{
              // alert(cl[0].Message);
               navigator.notification.alert(cl[0].Message,null,"提示","确定");
               }
               //               setTimeout($("#id-task-content").html(cl),200);
               //
               //               document.getElementById("ApprovalControl_BtnConfirm").onclick=BtnConfirm;
               //               document.getElementById("ApprovalControl_BtnCancel").onclick=BtnCancel;
               
               
               },
               error: function(xhr,textStatus,errMsg) {
               //alert(textStatus);
               navigator.notification.alert(textStatus,null,"提示","确定");
               $.mobile.hidePageLoadingMsg();
               }
               });
        
    }
    
}
function BtnCancel1(){
    //    console.log("========"+url);
    var SerialNumber=document.getElementsByName("serialNumber").value;
    var serverUrl=localStorage.getItem("serverUrl");
    var username=localStorage.getItem("userName");
    
    var MessageText=document.getElementById("ApprovalControl_TextAreaComment").value;
    console.log(MessageText);
    console.log(SerialNumber);
    if (confirm("确认拒绝？")){
        console.log("in btncancel");
        $.ajax({
               url:serverUrl+"/api/Approval",
               type:"POST",
               data:{"UserAccount":username,"SerialNumber":SerialNumber,"MessageText":MessageText,"iPhoneApprovalActionType":"2"},
               dataType:"json",
               success:function(cl){
               $.mobile.hidePageLoadingMsg();
               console.log(cl[0].result);
               if(cl[0].result=="True"){
               console.log(cl);
              // alert("提交成功！");
              navigator.notification.alert("提交成功！",null,"提示","确定");
               getTaskApproval(SerialNumber);
               }else{
              // alert(cl[0].Message);
               navigator.notification.alert(cl[0].Message,null,"提示","确定");
               }
               //               setTimeout($("#id-task-content").html(cl),200);
               //
               //               document.getElementById("ApprovalControl_BtnConfirm").onclick=BtnConfirm;
               //               document.getElementById("ApprovalControl_BtnCancel").onclick=BtnCancel;
               
               
               },
               error: function(xhr,textStatus,errMsg) {
               //alert(textStatus);
               navigator.notification.alert(textStatus,null,"提示","确定");
               $.mobile.hidePageLoadingMsg();
               }
               });
        
        
        
    }
}


function getnoticeDetail(SerialNumber){
    var serverUrl=localStorage.getItem("serverUrl");

    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");

                 $.mobile.showPageLoadingMsg('a', "Please wait..." );
                 var urlstr=serverUrl+"/iPhonewebPortal/BasicInfo.aspx?SerialNumber="+SerialNumber;
                 console.log(urlstr);
                 $.ajax({
                        url:urlstr,
                        username:username,
                        password:password,
                        data:{},
                        dataType:"html",
                        success:function(cl){
                        $.mobile.hidePageLoadingMsg();
                        console.log(cl);
                        $("#id-task-content").html(cl);
                       document.getElementById("ApprovalControl_BtnConfirm").attachEvent('onclick',BtnConfirm);
                        document.getElementById("ApprovalControl_BtnCancel").attachEvent('onclick',BtnCancel);
//                        alert(aArray.length);
                       

                        
                        },
                        error: function(xhr,textStatus,errMsg) {
                       // alert(textStatus);
                       navigator.notification.alert(textStatus,null,"提示","确定");
                        //alert(errMsg);
                        }
                        });

                

                 
             
       
}
function getnoticeSuggestion(SerialNumber){
    var serverUrl=localStorage.getItem("serverUrl");
       //    setTimeout(gethtml(SerialNumber),20000);
    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");


    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    var urlstr=serverUrl+"/iPhoneWebPortal/AuditList.aspx?SerialNumber="+SerialNumber;
    console.log(urlstr);
    $.ajax({
           url:urlstr,
           
           data:{},
           dataType:"html",
           success:function(cl){
           console.log(cl)
           $("#id-task-content").html(cl);
           
           $.mobile.hidePageLoadingMsg();
           },
           error: function(xhr,textStatus,errMsg) {
          // alert(textStatus);
           $.mobile.hidePageLoadingMsg();
           }
           });
    
}



function getnoticeAccessory(SerialNmber){
   var serverUrl=localStorage.getItem("serverUrl");
    
    var username=localStorage.getItem("userName");
    var password=localStorage.getItem("userPwd");
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
  

    var urlstr=serverUrl+"/iPhoneWebPortal/Attachment.aspx?SerialNumber="+SerialNmber;
//    var urlstr="https://baolong.capitaretail.com.cn/iWorkflowPortal/OpenFile.aspx?filepath=%5Cfiles%5C2012%5C02%5C14%5Ca4d502d9-ab20-4186-96f3-1ed26884b177.pdf";

    console.log(urlstr);

    $.ajax({
           url:urlstr,
           username:username,
           password:password,
           data:{},
           dataType:"html",
           success:function(cl){
           console.log(cl);
//            Cordova.exec(null,null,"Auth", "AuthUrl",[localStorage.getItem("userName"),localStorage.getItem("userPwd"),"https://baolong.capitaretail.com.cn"]);
            $("#id-task-content").html(cl);
           
           $.mobile.hidePageLoadingMsg();
           var aArray = document.getElementById("id-task-content").getElementsByTagName("a");
           for(var i=0;i<aArray.length;i++){
           
           var aId=aArray[i].id;
           var href=aArray[i].href;
           console.log(aId);
           console.log(href);
           }
       
           
           

           },
           error: function(xhr,textStatus,errMsg) {
          // alert(textStatus);
          navigator.notification.alert(textStatus,null,"提示","确定");
          // alert(errMsg);
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

function gethtml(SerialNumber){
    alert("in gethtml    liuyifei");
    
//    var url="http://liuyifei:simon_401e@10.154.128.13/iPhoneWebPortal/BasicInfo.aspx?SerialNumber="+SerialNumber;
//    // https://liuyifei:simon_401e@imoffice.capitaretail.com.cn/iPhoneWebPortal/BasicInfo.aspx?SerialNumber=2045-HR-HROB-00009443
//    alert(url);
//    $.get(url, {}, function(resp) {
//          console.log(resp);
//          $("#id-noticeDetails-content").html(resp);
//          
//          });
//    alert("out gethtml");
}