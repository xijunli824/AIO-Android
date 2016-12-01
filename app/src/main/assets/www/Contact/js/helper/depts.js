

function setDeptList(CorpId,tokenkey){
    
    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:"http://192.168.1.120/api/GetDeptUserList",
           type:"POST",
           data:{"CorpId":CorpId,"tokenkey":tokenkey},
           dataType:"json",
           success:function(cl){
           oListView=$("#id-dept-listView"),
           childs=[];
           oListView.html("");
          
           for(var i=0;i<cl[0].DeptList.length;i++){
           var depObj = cl[0].DeptList[i];
           
           for(var j=0;j<depObj.UserList.length;j++){
           var userObj = depObj.UserList[j];
           childs.push("<li class=\"area_"+i+"\" style=\"overflow:hidden;display:block;\" data-transition=\"slide\" data-icon=\"false\"><a href=\"contactDetail.html?contactId="+userObj.UserId+"\" data-transition=\"slide\"><div class=\"ui-listview-row-title\">"+userObj.EmpName+"   </div><div class=\"ui-listview-row-content\">"+"</div></a><div><a href=\"tel:"+userObj.Tel+"\" data-icon=\"tel\"  data-role=\"button\">"+userObj.Tel+"</a></div></li>");
           }
           oListView.append("<li data-role=\"list-divider\" onClick=\"hideArea('area_"+i+"')\"><input id =\"area_"+i+"\" value =\"show\" type=\"hidden\" />"+depObj.DeptName+"</li>");
           oListView.append(childs);
           childs=[];
           }
           
           oListView.append(childs.join(""));
           oListView.listview("refresh");
           for(var i=0;i<cl.length;i++){
           hideArea("area_"+i+"");
           }
           $.mobile.hidePageLoadingMsg();
           },
           error: function(xhr,textStatus,errMsg) {
           
           alert(textStatus);
           
           }
           });

    
    
}
