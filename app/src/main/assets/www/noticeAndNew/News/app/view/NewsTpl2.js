/**
 * Created with JetBrains WebStorm.
 * User: issuser
 * Date: 13-6-6
 * Time: 下午8:47
 * To change this template use File | Settings | File Templates.
 */
Ext.define('iNews.view.NewsTpl2',{
    extend:'Ext.Container',
    xtype:'newstpl2view',

    config:{
        scrollable:false,
        layout:'vbox',
        cls:'tplCls',
        items:[{
            xtype:'toolbar',
            docked:'top',
            cls:'customToolBar',
            title:'新闻公告',
            items:[
                {
                    xtype:'button',
                    ui:'action',
					text:'返回',
                    cls:'backBtnCls',
                    handler:function(){
                        this.up('newstpl2view').fireEvent("tpl2back",this);
                    }
                }
           ]
        },{
            xtype:'container',
            flex:1,
            layout:'hbox',
            cls:'lineCls',
            scrollable:false,
            items:[{
                xtype:'container',
                flex:1,
                itemId:'line2_left',
                cls:'blockCls',
                tpl:
                    '<div class="itemTitle">{Title}</div>'+
					'<tpl if="ColumnId == 2">'+
						'<div class="itemTime">新闻&nbsp;{PublishTime}</div>'+
					'</tpl>'+
					'<tpl if="ColumnId == 4">'+
						'<div class="itemTime">公告&nbsp;{PublishTime}</div>'+
					'</tpl>',
                listeners:{
                    'tap':{
                        fn : function() {
                            var record = this.getData() == null ? "":this.getData();
                            this.up('newstpl2view').fireEvent("line2_left_tap",this,record);
                        },
                        element : 'element'
                    }
                }
            },{
                xtype:'container',
                flex:1,
                itemId:'line2_right',
                cls:'blockCls',
                tpl:
                    '<div class="itemTitle">{Title}</div>'+
					'<tpl if="ColumnId == 2">'+
						'<div class="itemTime">新闻&nbsp;{PublishTime}</div>'+
					'</tpl>'+
					'<tpl if="ColumnId == 4">'+
						'<div class="itemTime">公告&nbsp;{PublishTime}</div>'+
					'</tpl>',
                listeners:{
                    'tap':{
                        fn : function() {
                            var record = this.getData() == null ? "":this.getData();
                            this.up('newstpl2view').fireEvent("line2_right_tap",this,record);
                        },
                        element : 'element'
                    }
                }
            }]
        },
          {
            xtype:'container',
            flex:2,
            itemId:'line1',
            cls:'lineCls',
            scrollable:false,
             tpl:'<div style="position:relative;height:188px;background-image:url(resources/images/pic_banner.png);background-repeat:no-repeat;background-size:100% 100%;">' +
                '<div style="padding:10px;top:69%;position:absolute;width: 100%;height: 60px;background-color: black;opacity: 0.5;color:white;font-size: 1.4em;">'+
					'<div>{Title}</div>'+
					'<div class="line1Cls"><img width="11px" height="11px" src="./resources/images/icon_date.png">&nbsp;{PublishTime}</div>'+
				'</div>' +
                '<img width="100%" height="100%" src="{ImgUrl}" />' +
             '</div>',
            listeners:{
                'tap':{
                    fn : function() {
                        var record = this.getData() == null ? "":this.getData();
                        this.up('newstpl2view').fireEvent("line1_tap",this,record);
                    },
                    element : 'element'
                }
            }
        },
          {
            xtype:'container',
            flex:1,
            layout:'hbox',
            scrollable:false,
            cls:'lineCls',
            items:[{
                xtype:'container',
                flex:1,
                itemId:'line3_left',
                cls:'blockCls',
                tpl:
                    '<div class="itemTitle">{Title}</div>'+
					'<tpl if="ColumnId == 2">'+
						'<div class="itemTime">新闻&nbsp;{PublishTime}</div>'+
					'</tpl>'+
					'<tpl if="ColumnId == 4">'+
						'<div class="itemTime">公告&nbsp;{PublishTime}</div>'+
					'</tpl>',
                listeners:{
                    'tap':{
                        fn : function() {
                            var record = this.getData() == null ? "":this.getData();
                            this.up('newstpl2view').fireEvent("line3_left_tap",this,record);
                        },
                        element : 'element'
                    }
                }
            },{
                xtype:'container',
                flex:1,
                itemId:'line3_right',
                cls:'blockCls',
                tpl:
                    '<div class="itemTitle">{Title}</div>'+
					'<tpl if="ColumnId == 2">'+
						'<div class="itemTime">新闻&nbsp;{PublishTime}</div>'+
					'</tpl>'+
					'<tpl if="ColumnId == 4">'+
						'<div class="itemTime">公告&nbsp;{PublishTime}</div>'+
					'</tpl>',
                listeners:{
                    'tap':{
                        fn : function() {
                            var record = this.getData() == null ? "":this.getData();
                            this.up('newstpl2view').fireEvent("line3_right_tap",this,record);
                        },
                        element : 'element'
                    }
                }
            }]
        },{
            xtype:'container',
            flex:1,
            itemId:'line4',
            cls:'lineCls',
            style:'padding:10px',
            scrollable:false,
			tpl:
                    '<div class="itemTitle">{Title}</div>'+
					'<tpl if="ColumnId == 2">'+
						'<div class="itemTime">新闻&nbsp;{PublishTime}</div>'+
					'</tpl>'+
					'<tpl if="ColumnId == 4">'+
						'<div class="itemTime">公告&nbsp;{PublishTime}</div>'+
					'</tpl>',
            listeners:{
                'tap':{
                    fn : function() {
                        var record = this.getData() == null ? "":this.getData();
                        this.up('newstpl2view').fireEvent("line4_tap",this,record);
                    },
                    element : 'element'
                }
            }
        },{
            xtype:'container',
            flex:0.2,
            scrollable:false,
            style:'background-color:transparent',
            html:''
        }]
    },
    setRecords:function(records){
        console.log("in template2 setRecords method:",records);
        var me = this,
            line1 = this.down("#line1"),
            line2_left = this.down("#line2_left"),
            line2_right = this.down("#line2_right"),
            line3_left = this.down("#line3_left"),
            line3_right = this.down("#line3_right"),
            line4 = this.down("#line4");
        line1.setData({"NewsId":records[0].NewsId,"ColumnId":records[0].ColumnId,"Title":records[0].Title,"PublishTime":records[0].PublishTime,"ImgUrl":records[0].ImageUrl});
        line2_left.setData({"NewsId":records[1].NewsId,"ColumnId":records[1].ColumnId,"Title":records[1].Title,"PublishTime":records[1].PublishTime,"ImgUrl":records[1].ImageUrl});
        line2_right.setData({"NewsId":records[2].NewsId,"ColumnId":records[2].ColumnId,"Title":records[2].Title,"PublishTime":records[2].PublishTime,"ImgUrl":records[2].ImageUrl});
        line3_left.setData({"NewsId":records[3].NewsId,"ColumnId":records[3].ColumnId,"Title":records[3].Title,"PublishTime":records[3].PublishTime,"ImgUrl":records[3].ImageUrl});
        line3_right.setData({"NewsId":records[4].NewsId,"ColumnId":records[4].ColumnId,"Title":records[4].Title,"PublishTime":records[4].PublishTime,"ImgUrl":records[4].ImageUrl});
        line4.setData({"NewsId":records[5].NewsId,"ColumnId":records[5].ColumnId,"Title":records[5].Title,"PublishTime":records[5].PublishTime,"ImgUrl":records[5].ImageUrl});
    }
})