/**
 * Created with JetBrains WebStorm.
 * User: issuser
 * Date: 13-6-6
 * Time: 下午8:47
 * To change this template use File | Settings | File Templates.
 */
Ext.define('iNews.view.DetailView',{
    extend:'Ext.Container',
    xtype:'detailview',

    config:{
        scrollable:true,
        style:'background-color: #ffffff;',
        html:"<div id='title' class='titleCls'></div>" +
            "<div id='content' class='contentCls'></div>",
        items:[{
            xtype:'toolbar',
            docked:'top',
            cls:'customToolBar',
            title:'详情',
            items:[
                {
                    xtype:'button',
                    ui:'action',
                    text:'返回',
					cls:'backBtnCls',
                    //iconMask:true,
                    //icon:'resources/images/pic.png',
                    handler:function(){
                        this.up('detailview').fireEvent("detailback",this);
                    }
                }
            ]
        }]
    },
    loadPage:function(){
       // this.innerElement
    }
})