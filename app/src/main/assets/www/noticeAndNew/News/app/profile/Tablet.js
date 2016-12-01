/**
 * Created with JetBrains WebStorm.
 * User: DK
 * Date: 12-11-28
 * Time: 上午10:26
 * To change this template use File | Settings | File Templates.
 */
Ext.define('iTrack.profile.Tablet', {
    extend: 'Ext.app.Profile',

    isActive: function() {
        return Ext.os.is.Tablet;
    },

    launch: function() {
        //Ext.create('AQuery.view.tablet.Main');
        this.callParent();
    }
});