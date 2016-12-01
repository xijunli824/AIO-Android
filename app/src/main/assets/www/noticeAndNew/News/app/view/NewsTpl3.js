/**
 * Created with JetBrains WebStorm.
 * User: issuser
 * Date: 13-6-6
 * Time: 下午8:47
 * To change this template use File | Settings | File Templates.
 */
Ext.define('iNews.view.NewsTpl3',{
    extend:'Ext.Container',
    xtype:'newstpl3view',

    config:{
        scrollable:false,
        layout:'vbox',
        items:[{
            xtype:'toolbar',
            docked:'top',
            cls:'customToolBar',
            title:'新闻',
            items:[
                {
                    xtype:'button',
                    ui:'plain',
                    cls:'backBtnCls',
                    iconMask:true,
                    icon:'resources/images/pic.png',
                    handler:function(){
                        this.up('newstpl3view').fireEvent("tpl3back",this);
                    }
                }
            ]
        },{
            xtype:'container',
            flex:2,
            itemId:'line1',
            scrollable:false,
            style:'border:1px solid black',
            tpl:
                '<div>{Content}</div>'+
               '<div>{LastDateTime}</div>',
            listeners:{
                'tap':{
                    fn : function() {
                        var record = this.getData() == null ? "":this.getData();
                        this.up('newstpl3view').fireEvent("line1_tap",this,record);
                    },
                    element : 'element'
                }
            }
        },{
            xtype:'container',
            flex:1,
            layout:'hbox',
            scrollable:false,
            items:[{
                xtype:'container',
                flex:1,
                itemId:'line2_left',
                style:'border:1px solid black',
                tpl:
                    '<div>{Content}</div>'+
                        '<div>{LastDateTime}</div>',
                listeners:{
                    'tap':{
                        fn : function() {
                            var record = this.getData() == null ? "":this.getData();
                            this.up('newstpl3view').fireEvent("line2_left_tap",this,record);
                        },
                        element : 'element'
                    }
                }
            },{
                xtype:'container',
                flex:1,
                itemId:'line2_right',
                style:'border:1px solid black',
                tpl:
                    '<div>{Content}</div>'+
                        '<div>{LastDateTime}</div>',
                listeners:{
                    'tap':{
                        fn : function() {
                            var record = this.getData() == null ? "":this.getData();
                            this.up('newstpl3view').fireEvent("line2_right_tap",this,record);
                        },
                        element : 'element'
                    }
                }
            }]
        },{
            xtype:'container',
            flex:1,
            itemId:'line3',
            layout:'hbox',
            scrollable:false,
            style:'border:1px solid black',
            items:[{
                xtype:'container',
                flex:1,
                itemId:'line3_left',
                style:'border:1px solid black',
                tpl:
                    '<div>{Content}</div>'+
                        '<div>{LastDateTime}</div>',
                listeners:{
                    'tap':{
                        fn : function() {
                            var record = this.getData() == null ? "":this.getData();
                            this.up('newstpl3view').fireEvent("line3_left_tap",this,record);
                        },
                        element : 'element'
                    }
                }
            },{
                xtype:'container',
                flex:1,
                itemId:'line3_right',
                style:'border:1px solid black',
                tpl:
                    '<div>{Content}</div>'+
                        '<div>{LastDateTime}</div>',
                listeners:{
                    'tap':{
                        fn : function() {
                            var record = this.getData() == null ? "":this.getData();
                            this.up('newstpl3view').fireEvent("line3_right_tap",this,record);
                        },
                        element : 'element'
                    }
                }
            }]
        },{
            xtype:'container',
            flex:1,
            itemId:'line4',
            scrollable:false,
            style:'border:1px solid black',
            tpl:
                '<div>{Content}</div>'+
                    '<div>{LastDateTime}</div>',
            listeners:{
                'tap':{
                    fn : function() {
                        var record = this.getData() == null ? "":this.getData();
                        this.up('newstpl1view').fireEvent("line4_tap",this,record);
                    },
                    element : 'element'
                }
            }
        },{
            xtype:'container',
            flex:0.2,
            scrollable:false,
            style:'background-color:transparent'
        }]
    },
    setRecords:function(records){
        console.log("in template3 setRecords method:",records);
        var me = this,
            line1 = this.down("#line1"),
            line2_left = this.down("#line2_left"),
            line2_right = this.down("#line2_right"),
            line3_left = this.down("#line3_left"),
            line3_right = this.down("#line3_right"),
            line4 = this.down("#line4");
        line1.setData({"Content":records.NewsList[0].Content});
        line2_left.setData({"Content":records.NewsList[1].Content});
        line2_right.setData({"Content":records.NewsList[2].Content});
        line3_left.setData({"Content":records.NewsList[3].Content});
        line3_right.setData({"Content":records.NewsList[4].Content});
        line4.setData({"Content":records.NewsList[5].Content});
    }
})