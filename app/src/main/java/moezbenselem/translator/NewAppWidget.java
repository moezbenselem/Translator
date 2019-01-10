package moezbenselem.translator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private static final String MyOnClick = "myOnClickTag";
    static RemoteViews views;
    static int appWId;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        appWId = appWidgetId;
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        //views.setTextViewText(R.id.item_output, widgetText);
        views.setOnClickPendingIntent(R.id.btSpeach_w,
                getPendingSelfIntent(context, MyOnClick));


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context,NewAppWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (MyOnClick.equals(intent.getAction())){

            views.setTextViewText(R.id.item_output_w,"just clicked !!");
            System.out.println("just clicked");
            //appWidgetManager.updateAppWidget(appWidgetId, views);
            // Update widgets
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AppWidgetManager.getInstance(context).updateAppWidget(
                    new ComponentName(context, NewAppWidget.class),views);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
            this.onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
}

