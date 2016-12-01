//function getContactList(deptId){
//    $.mobile.showPageLoadingMsg('a', "Please wait..." );
//    $.ajax({
//           url:"http://10.164.2.58/MobileWebService.aspx?type=3&deptid="+deptId,
//           type:"GET",
//           data:{},
//           dataType:"json",
//           success:function(cl){
//           oListView=$("#id-contact-listView"),
//           childs=[];
//           oListView.html("");
//           var dataObj = cl[0];
//           for(var i=0;i<dataObj.UserList.length;i++){
//           var userObj = dataObj.UserList[i];
//           childs.push("<li style=\"overflow:hidden\"><a href=\"contactDetail.html?contactId="+userObj.UserID+"\" data-transition=\"slide\">"+userObj.UserName+"</a></li>");
//           oListView.append(childs.join(""));
//           childs=[];
//           }
//           oListView.append(childs.join(""));
//           oListView.listview("refresh");
//           $.mobile.hidePageLoadingMsg();
//           },
//           error: function(xhr,textStatus,errMsg) {
//           alert(textStatus);
//           }
//           });
//}
function getContactList(CorpId){
    alert("eeeee");
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:"http://10.164.2.58/api/GetDeptUserList&CorpId="+CorpId,
           type:"POST",
           data:{"CorpId":CorpId,"tokenkey":tokenkey},
           dataType:"json",
           success:function(cl){
           oListView=$("#id-contact-listView"),
           childs=[];
           oListView.html("");
           var dataObj = cl[0];
           for(var i=0;i<dataObj.UserList.length;i++){
           var userObj = dataObj.UserList[i];
           childs.push("<li style=\"overflow:hidden\"><a href=\"contactDetail.html?contactId="+userObj.UserID+"\" data-transition=\"slide\">"+userObj.UserName+"</a></li>");
           oListView.append(childs.join(""));
           childs=[];
           }
           oListView.append(childs.join(""));
           oListView.listview("refresh");
           $.mobile.hidePageLoadingMsg();
           },
           error: function(xhr,textStatus,errMsg) {
           alert(textStatus);
           }
           });
}
