var userID = "-1";
function getContactDetail(contactId,tokenkey){

    $.mobile.showPageLoadingMsg('a', "Please wait..." );
    $.ajax({
           url:"https://10.164.2.58/api/GetSigleEmp",
           type:"POST",
           data:{"userid":contactId,"tokenkey":tokenkey},
           dataType:"json",
           success:function(cl){
           
           oDetailView=$("#id-contact-detailView"),
           childs=[];
           oDetailView.html("<style>.ui-li-desc{white-space: normal;}</style>");
           oDetailView.append("<li data-icon=\"false\"><div><div style=\"width:90px;height:90px;float:left\"><img style=\"width:80px;height:80px\" src=\""+cl[0].User[0].UserImgUrl+"\"></div><div style=\"height:90px;\"><p>"+cl[0].User[0].UserName+"</p><p>"+cl[0].User[0].CorpName+"</p><p>"+cl[0].User[0].DeptName+"</p></div></div></li>");
           oDetailView.append("<li><a href=\"\" data-role=\"button\">"+cl[0].User[0].UserMobile+"</a></li>");
           oDetailView.append("<li><a href=\"\" data-role=\"button\">"+cl[0].User[0].UserTel+"</a></li>");
           oDetailView.append("<li><a href=\"\" data-role=\"button\">"+cl[0].User[0].UserEmail+"</a></li>");
           oDetailView.listview("refresh");
           $.mobile.hidePageLoadingMsg();
           userID = cl[0].User[0].UserName;
           },
           error: function(xhr,textStatus,errMsg) {
           alert(textStatus);
           }
           });
}
function addToFav(){
    alert(userID);
}
