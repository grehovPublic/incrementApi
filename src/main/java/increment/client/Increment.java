package increment.client;

import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Value;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Timer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Increment implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
    private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " 
			+ "connection and try again.";
	
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
        //String url = URL.encode(proxyUrl);           
        RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT, 
                "http://localhost:8000/increment/proxy");
        final int toIncrement = Random.nextInt();
        
        updateIncrementLabels(toIncrement, toIncrement);
        
        try {
        Request request = builder.sendRequest(Integer.toString(toIncrement), 
            new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    displayError("Couldn't retrieve JSON");
                }       
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        updateIncrementLabels(toIncrement, Integer.valueOf(response.getText()));
                    } else {
                        displayError("Couldn't retrieve JSON (" + response.getStatusText()+ ")");
                    }
                }
            });
        } catch (RequestException e) {
            displayError("Couldn't retrieve JSON");
        }   
    }
    
    
    /**
     * Update increment labels .
     *
     * @param toIncrement number before increment
     * @param increment number after increment.
     */
    private void updateIncrementLabels(Integer toIncrement, Integer incremented) {

//        toIncrementLabel.setText(toIncrement.toString());
//        incrementedLabel.setText(incremented.toString());
        
        toIncrementLabel.setText("888");
        incrementedLabel.setText("42");

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
