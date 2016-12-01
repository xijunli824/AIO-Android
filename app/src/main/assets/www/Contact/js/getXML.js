
//指定xml文件的来源地址
function getxml(){
    alert("in getXML");
    　var xmlDoc;
    // code for IE
    if (window.ActiveXObject)
    {
        xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
        xmlDoc.async=true;
        xmlDoc.load('./xml/English.xml');
    }
    // code for Mozilla, Firefox, Opera, etc.
    else if (typeof document.implementation.createDocument != "undefined")
    {
        alert("in else");
        try{
        xmlDoc=document.implementation.createDocument("","",null);
            xmlDoc.load('./xml/English.xml');
        }catch{
        
        }
        alert("ok");
    }
    else
    {
        alert('Your browser cannot handle this script');
    }
  
    
  

    alert("ok");
}