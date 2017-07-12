package increment.client;

import org.springframework.beans.factory.annotation.Value;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Increment implements EntryPoint {
	
    private static final int REFRESH_INTERVAL = 5000;
    
    @Value("${increment.url.proxy-php}")
    private String proxyUrl ;
     
    private final VerticalPanel toIncementPanel = new VerticalPanel();
    private final Label toIncrementLabel = new Label();
    
    private final VerticalPanel incrementedPanel = new VerticalPanel();
    private final Label incrementedLabel = new Label();
    
    private Label errorMsgLabel = new Label();
    
    private final Timer refreshTimer;
    
    public Increment() {
        refreshTimer = new Timer() {
          @Override
          public void run() {
              refreshIncrementor();
          }
        };
        refreshTimer.scheduleRepeating(REFRESH_INTERVAL);             
    }
    

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {      
        errorMsgLabel.setStyleName("errorMessage");
        errorMsgLabel.setVisible(false);

        toIncementPanel.add(toIncrementLabel);
        incrementedPanel.add(incrementedLabel);
        incrementedPanel.add(errorMsgLabel);     
        
        RootPanel.get("toIncrement").add(toIncementPanel);
        RootPanel.get("incremented").add(incrementedPanel);       
    }
    
    /**
     * Generate random number and increment it with remote
     * increment API.
     */
    private void refreshIncrementor() {  
        final int toIncrement = Random.nextInt();
        String url = proxyUrl + Integer.toString(toIncrement);           
        url = URL.encode(url);  
        
        JsonpRequestBuilder builder = new JsonpRequestBuilder();
        builder.requestObject(url, new AsyncCallback<Incremented>() {
          public void onFailure(Throwable caught) {
            displayError("Couldn't retrieve JSON");
          }
          public void onSuccess(Incremented incremented) {
              updateIncrementLabels(Integer.toString(toIncrement), incremented.getIncremented());
          }
        }); 
    }
    
    /**
     * Update increment labels .
     *
     * @param toIncrement number before increment
     * @param increment number after increment.
     */
    private void updateIncrementLabels(String toIncrement, String stock) {
        toIncrementLabel.setText(toIncrement.toString());
        incrementedLabel.setText(stock);

        // Clear any errors.
        errorMsgLabel.setVisible(false);
    }
    
    /*
     * If can't get JSON, display error message.
     * @param error
     */
    private void displayError(String error) {
      errorMsgLabel.setText("Error: " + error);
      errorMsgLabel.setVisible(true);
    }
}

/**
 * Implementation of overlay type for incremented value.
 */
class Incremented extends JavaScriptObject {

    protected Incremented() { };   
    public final native String getIncremented() /*-{ return this.incremented; }-*/;
  }
