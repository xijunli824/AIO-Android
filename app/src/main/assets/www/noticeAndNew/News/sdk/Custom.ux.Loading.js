/**
 * @class Easymi.ux.st.Mask
 * @author Sam.zhbh<zhongbh@mibridge-tech.com>
 *
 *  Easymi.ux.st.Mask 该组件主要有两种用途:当发送ajax请求时，遮盖住面板告知用户正在请求中， 同时防止用户操作；提示用户操作的结果。需要以下文件:
 *  easymi.ux.st.gridmenu.css;
 *  images/failure.png、success.png、loading.gif;
 *
 *  实例如下:
 *
 *例子一.显示遮盖提示语“系统处理中……“
 *     @example
 *      Easymi.ux.st.Mask.show();
 *        setTimeout(function(){
 *        	Easymi.ux.st.Mask.hide();//2秒后，提示语隐藏
 *      },2000);
 *
 *例子二.显示遮盖提示语“数据正在加载中，请稍后……“并位置为顶部中间。
 *
 *      @example
 *      Easymi.ux.st.Mask.show({
 *         message:'数据正在加载中，请稍后……',position:'center top'
 *      });
 *
 *例子三.显示提示语“成功加载数据“，并5秒后自动隐藏并弹出提示框
 *
 *      @example
 *     Easymi.ux.st.Mask.showTip({
 *        message:'成功加载数据',
 *        type:'success',
 *        showTipTime:5,
 *        callback:function(){
 *             Ext.Msg.alert('显示结束');
 *        }
 *     });
 *
 *
 * ### 常用属性
 * * {@link #message} 遮盖时，此参数为设置显示提示的文字，当调用show方法，缺省值为“系统处理中……”；showTip方法中，此参数是必须设置。
 * * {@link #type} 此参数为showTip方法的必须设置类型，类型有success:显示时图标为打钩；failure:显示时图标为打叉，而loading则为加载图标（show方法缺省为loading）。
 * * {@link #showTipTime} 当调用showTip方法时，显示提示语的时间（单位为秒），缺省值为2秒。
 * * {@link #modal} 遮盖配置项，若为false则取消遮盖。
 * * {@link #position} 该参数有2个值, 第一个为水平方向的位置[left|center|right]，第二个为竖直方向的位置[top|center|bottom]；
 >缺省值为 “center center”，表示为遮盖文字居中，例如[left top]表示位置为左上角。
 * * {@link #showAnimation} 提示语显示的动画效果，showTip方法可选参数。
 >动画效果有[fadeIn/Out （淡入淡出）|popIn/Out（弹入弹出）|slideIn/Out（滑入滑出）|flip（垂直翻转）|cube（水平翻转）]
 * * {@link #hideAnimation} 提示框隐藏的动画效果，showTip方法可选参数。
 >动画效果有[fadeIn/Out （淡入淡出）|popIn/Out（弹入弹出）|slideIn/Out（滑入滑出）|flip（垂直翻转）|cube（水平翻转）]
 * * {@link #style} 提示框的CSS样式，缺省为空值。
 * * {@link #callback} 当调用showTip方法时，提示框完成时的回调函数。
 *
 *
 * ### 常用方法
 * * {@link #show} 显示遮盖提示框。当同时调用此方法多次，只显示最后执行方法。
 >   参数：
 >>    message:Sting（可选），缺省值为“系统处理中……“
 >>    type:Sting（可选），缺省值为“loading“
 >>    style:Sting（可选），CSS样式
 * * {@link #hide} 隐藏遮盖提示框。
 * * {@link #showTip} 显示遮盖提示语，并设置时间自动隐藏。当同时执行show和showTip方法，将会先执行showTip方法，后执行show方法。
 >   参数：
 >>    message:Sting（必选）
 >>    type:Sting（必选），若值为“success“设置打钩图标，值为“failure“为打叉图标
 >>    showTipTime: Number（可选），显示提示语的时间，单位为秒，缺省值为2秒
 >>    style:Sting（可选），CSS样式
 >>    position:String（可选）缺省值为“center center“，即居中位置显示
 >>    showAniation:String（可选）
 >>    hideAniation:String（可选）
 >>    callback:Function（可选）显示结束后，调用函数
 *
 */
Ext.define('Custom.ux.Loading',{
        extend:'Ext.Container',
        xtype:'customloading',

        statics:{
            selTime:0,
            infMsg:[]
        },

        config:{
            transparent: false,
            top:0,
            left:0,
            right:0,
            bottom:0,
            showAnimation:'',
            hideAnimation:'',
            showTipTime:2,
            style:'',
            position:'',
            baseCls:Ext.baseCSSPrefix+'easymi-mask',
            data:{
                type:'loading',
                message:'系统处理中……'
            },
            tpl:[
                '<div class="x-easymimask-{type}">',
                '<span>{message}</span>',
                '</div>'
            ],
            modal:true
        },
        constructor:function(){
            this.callParent();
        },
        setPara:function(config){
            var me = this;
            me.setData({
                message:config.message,
                type:config.type
            });

            //console.log(me.getModal());
            if(!config.modal){
                me.setModal(false);
                me.setTop('50%');
                me.setBottom('50%');
            }else{
                me.setModal(true);
                me.setTop(0);
                me.setBottom(0);
            }
            me.setStyle(config.style || '');
            me.setPosition(config.position || 'center center');
            me.setShowTipTime(config.showTipTime || 2);
            me.setShowAnimation({type:config.showAnimation,duration: 250,easing: 'ease-out'} || '');
            me.setHideAnimation({type:config.hideAnimation,duration: 250,easing: 'ease-out'} || '');
        },
        /**
         *  遮盖位置
         */
        setPosition:function(position){
            var me = this,
                arrPos;

            if(!position)  {
                return;
            }
            arrPos = position.split(' ');
            switch(arrPos[0]){
                case 'left':
                    me.setStyle('-webkit-box-pack:start;box-pack:start;padding-left: 2%;');
                    break;
                case 'right':
                    me.setStyle('-webkit-box-pack:end;box-pack:end;padding-right: 2%;');
                    break;
                default :
                    me.setStyle('-webkit-box-pack:center;');
                    break;
            }

            switch(arrPos[1]){
                case 'top':
                    me.setStyle('webkit-box-align: start;box-align: start;padding-top: 10%;');
                    break;
                case 'bottom':
                    me.setStyle('webkit-box-align: end;box-align: end;padding-bottom: 10%;');
                    break;
                default :
                    me.setStyle('webkit-box-align: center;');
                    break;
            }
        },
        /**
         *  回调函数
         */
        callback:function(selfcallback){
            if(selfcallback){
                selfcallback();
            }
        },
        /**
         *  显示组件
         */
        showComponent:function(config){
            var me = this,
                selfhide,animation = config.showAnimation;

            if(!this.getParent() && Ext.Viewport){
                Ext.Viewport.add(me);
                me.setStyle('display:none;');
            }

            selfhide =  me.getHidden();

            if(selfhide || selfhide === null){
                if(config.showAnimation){
                    me.onBefore({
                        hiddenchange: 'animateFn',
                        scope: me,
                        single: true,
                        args: [animation]
                    });
                }
                me.setHidden(false);
            }
        },
        /**
         *  控制show和showTip，以解决两者冲突
         */
        controlDisplay:function(config){
            var me = this,
                x = {},
                y = {},
                statics = this.statics();

            statics.infMsg.push(config);

            if(statics.infMsg.length >= 2 ){

                if(statics.infMsg[0].type == statics.infMsg[1].type ){
                    statics.infMsg.shift();
                    return false;
                }

                //立即终止当前计时时间
                clearTimeout(statics.selfTime);

                me.setHidden(true);
                if(statics.infMsg[0].type != 'loading') {
                    x = statics.infMsg[0];
                    y = statics.infMsg[1];
                }else{
                    y = statics.infMsg[0];
                    x = statics.infMsg[1];
                }

                me.setPara(x);
                me.showComponent(x);

                statics.selfTime = setTimeout(function(){
                    me.callback(x.callback);
                    me.setPara(y);
                    statics.infMsg = [];
                    clearTimeout(statics.selfTime);
                },x.showTipTime*1000);

                return true;
            }

            return false;
        },
        /**
         *  显示遮盖提示框。
         */
        show:function(initconfig){
            var me = this,
                statics = this.statics();
            tempconfig = {};

            initconfig = initconfig || {};

            if(initconfig.modal == undefined){
                initconfig.modal = true;
            }

            tempconfig = {
                message:initconfig.message|| '系统处理中……',
                type:'loading',
                style:initconfig.style || '',
                modal:initconfig.modal
            };

            if(me.controlDisplay(tempconfig)){
                return;
            }

            me.setPara(tempconfig);

            if(!this.getParent() && Ext.Viewport ){
                Ext.Viewport.add(me);
            }

            this.callParent();
        },
        /**
         *  显示遮盖提示语，并设置时间自动隐藏。
         */
        showTip:function(initconfig){
            var me = this,
                statics = this.statics(),
                tempconfig = {};

            //没有参数，立即返回
            if(!initconfig.message || !initconfig.type){
                console.error('错误：Easymi.ux.Mask.showTip()缺少必须参数');
                return;
            }

            tempconfig = {
                message:initconfig.message,
                type:initconfig.type || 'success',
                style:initconfig.style || '',
                showTipTime:initconfig.showTipTime || 2,
                position:initconfig.position || 'center center',
                showAnimation:initconfig.showAnimation || '',
                hideAnimation:initconfig.hideAnimation || ''  ,
                callback:initconfig.callback || ''
            };

            if(me.controlDisplay(tempconfig)){
                return;
            }

            me.setPara(tempconfig);
            me.showComponent(tempconfig);

            statics.selfTime = setTimeout(function(){
                me.callback(tempconfig.callback);
                me.hide();
            },tempconfig.showTipTime*1000);

        },
        /**
         *  隐藏遮盖提示框。
         */
        hide:function(){
            var statics = this.statics();
            clearTimeout(statics.selTime);
            statics.infMsg = [];
            this.callParent();
        }
    },function(EasymiMask){
        /**
         *  静态变量
         */
        Ext.onSetup(function(){
            Custom.ux.Loading = new EasymiMask;
        });
    }
);

