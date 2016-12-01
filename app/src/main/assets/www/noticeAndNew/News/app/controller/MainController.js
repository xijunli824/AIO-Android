Ext.define('iNews.controller.MainController',{
    extend:'Ext.app.Controller',

    config:{
        refs: {
            mainView:"mainpanel",
            //
            carouselView:{
                selector:"carouselview",
                xtype:"carouselview",
                autoCreate:true
            },
            //tpl1
            newsView1:{
                selector:"newstpl1view",
                xtype:"newstpl1view",
                autoCreate:true
            },
            //tpl2
            newsView2:{
                selector:"newstpl2view",
                xtype:"newstpl2view",
                autoCreate:true
            },
            //tpl3
            newsView3:{
                selector:"newstpl3view",
                xtype:"newstpl3view",
                autoCreate:true
            },
            //detailContent view
            detailView:{
                selector:"detailview",
                xtype:"detailview",
                autoCreate:true
            },
            //
            block1:'container[itemId="line1"]',
            block2:'container[itemId="line2_left"]',
            block3:'container[itemId="line2_right"]',
            block4:'container[itemId="line3_left"]',
            block5:'container[itemId="line3_right"]',
            block6:'container[itemId="line4"]'
        },
        control:{
            carouselView:{
                activeitemchange:'onChangeItem'
            },
            newsView1:{
                tpl1back:'onExitApp',
                line1_tap:'onBlockTap',
                line2_left_tap:'onBlockTap',
                line2_right_tap:'onBlockTap',
                line3_left_tap:'onBlockTap',
                line3_right_tap:'onBlockTap',
                line4_tap:'onBlockTap'
            },
            newsView2:{
                tpl2back:'onExitApp',
                line1_tap:'onBlockTap',
                line2_left_tap:'onBlockTap',
                line2_right_tap:'onBlockTap',
                line3_left_tap:'onBlockTap',
                line3_right_tap:'onBlockTap',
                line4_tap:'onBlockTap'
            },
            newsView3:{
                tpl3back:'onExitApp',
                line1_tap:'onBlockTap',
                line2_left_tap:'onBlockTap',
                line2_right_tap:'onBlockTap',
                line3_left_tap:'onBlockTap',
                line3_right_tap:'onBlockTap',
                line4_tap:'onBlockTap'
            },
            detailView:{
                detailback:'onDetailBack'
            }
        }
    },
    //Exit App
    onExitApp:function(obj){
        Cordova.exec(null,null,"Application", "exit",["5"]);
    },
    onDetailBack:function(obj){
        var me = this,
            main = me.getMainView();
        main.pop();
        //逻辑
    },
    onBlockTap:function( obj, record){
        var me = this,
            main = me.getMainView(),
            detailview = me.getDetailView(),
            content,
            screenHeight=Ext.Viewport.getWindowHeight(),
            screenWidth=Ext.Viewport.getWindowWidth();
        main.push(detailview);
        //get news detail
        Custom.ux.Loading.show({modal:true});
        Ext.Ajax.request({
            url:domain+"/api/GetNewsORBulletinInfo",
            method:'POST',
            params:{
                newid:record.NewsId,
                tokenkey:"R3TWGNeql8k3bamyXzhURhwbbIxi6z56"
            },
            success:function(response){
				Custom.ux.Loading.hide();
                var resObj = Ext.decode(response.responseText);
                content = resObj[0].News[0].ContentText;
                content=content.replace(/&gt;/ig,">");
                content=content.replace(/&lt;/ig,"<");
                content=content.replace(/&nbsp;/ig," ");
                content=content.replace(/&#39;/ig,"\'");
                content=content.replace(/{/ig,"{");
                content=content.replace(/，/ig,",");
                content=content.replace(/：/ig,":");
                content=content.replace(/‘/ig,"'");
                content=content.replace(/”/ig,"'");
                content=content.replace(/&quot;/ig,"\"");
                document.getElementById("title").innerHTML = resObj[0].News[0].Title;
                document.getElementById("content").innerHTML = content;
                var imgArray = document.getElementById("content").getElementsByTagName("img");
                for(var i=0;i<imgArray.length;i++){
                    var img=imgArray[i],
                        imgHeight = img.getAttribute("height"),
                        imgWidth = img.getAttribute("Width");
                    if(imgWidth && imgHeight){
                        if(parseInt(imgWidth) > screenWidth){
                            var multiple = parseInt(parseInt(imgWidth)/(Ext.Viewport.getWindowWidth()*0.8));
                            img.style.width=Ext.Viewport.getWindowWidth()*0.8+"px";
                            img.style.height=parseInt(imgHeight)/multiple+"px";
                        }
                    }else{
                       // img.style.width=Ext.Viewport.getWindowWidth()*0.8+"px";
                       //s img.style.height=Ext.Viewport.getWindowHeight()*0.4+"px";
                    }
                }
            },
            failure:function(){
             Custom.ux.Loading.hide();
             console.log("网络不可用!");
            }
        });
    },
    onChangeItem:function(cn,value,oldvalue,opt){
        var me = this,
            index = cn.getInnerItems().length;
        if(!oldvalue){
            return ;
        }
        if(index-1 == cn.getActiveIndex()){
            Custom.ux.Loading.show({modal:true});
            Ext.Ajax.request({
                url:domain+'/api/GetBulletinAndNewsListNew',
                method:'POST',
                params:{
                    startpage:index,
                    perpage:6,
                    tokenkey:token
                },
                success:function(responese){
                    Custom.ux.Loading.hide();
                    var resObj = Ext.decode(responese.responseText),
                        carousel = Ext.ComponentQuery.query('carouselview')[0],
                        newsTpl;
                    if(resObj[0].result == "True"){
						if(resObj[0].NewsList.length == 0){
							alert("已是最后一页!");
						}else{
							for(var i=0;i<resObj[0].NewsList.length;i++){
								switch(i%2){
									case 0:
										if(resObj[0].NewsList[i].length == 6){
											newsTpl  =  Ext.create("iNews.view.NewsTpl1",{});
											newsTpl.setRecords(resObj[0].NewsList[i]);									
										}
									break;
									case 1:
										if(resObj[0].NewsList[i].length == 6){
											newsTpl  =  Ext.create("iNews.view.NewsTpl2",{});
											newsTpl.setRecords(resObj[0].NewsList[i]);									
										}
									break;
								}
								carousel.add(newsTpl);
							}						
						}
                    }else{
                        console.log("请求失败!");
                    }
                },
                failure:function(){
                    Custom.ux.Loading.hide();
                    console.log("网络不可用!");
                }
            });
        }
    }
});

