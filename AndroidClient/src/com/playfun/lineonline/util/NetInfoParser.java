package com.playfun.lineonline.util;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class NetInfoParser {

	// Network methods for HomePage
	/**
	 * Get the list of playgrounds based on the selected region
	 * 
	 * @param region
	 * @return the list of playgrounds in the selected region
	 */
	public static JSONArray getPlayGroundList(String region)
			throws SocketTimeoutException, JSONException {

		// Get JSON array from the server
		JSONArray getResult = null;
		getResult = new JSONArray(Communicator.sendGet("?m=Index&a=show"));

		return getResult;
	}

	/**
	 * Get the list of playgrounds whose names match the searchField
	 * 
	 * @param searchField
	 * @return the list of playgrounds
	 */
	public static JSONArray searchPlayground(String searchField)
			throws SocketTimeoutException, JSONException {

		// Get JSON array from the server
		JSONArray getResult = null;
		getResult = new JSONArray(Communicator.sendPost("?m=Index&a=search",
				"&searchName=" + searchField));
		return getResult;
	}

	// Network methods for BookActivity
	/**
	 * List all the attractions in the designated playground
	 * 
	 * @param playgroundID
	 * @return the list of attractions
	 */
	public static JSONArray getAttractionList(String playgroundID)
			throws SocketTimeoutException, JSONException {
		// Get JSON array from the server
		JSONArray getResult = null;
		getResult = new JSONArray(
				Communicator.sendGet("?m=Book&a=show&playgroundID="
						+ playgroundID));
		return getResult;
	}

	/**
	 * Show the detail info of the attraction
	 * 
	 * @param attrID
	 * @return
	 */
	public static JSONObject showAttractionDetail(String attrID)
			throws SocketTimeoutException, JSONException {
		JSONObject getResult = null;
		getResult = new JSONObject(
				Communicator.sendGet("?m=Book&a=select&attractionID=" + attrID));
		return getResult;
	}
	
	public static String getAppointmentNum(String userID)
		throws SocketTimeoutException {
		return Communicator.sendGet("?m=Wait&a=show_app_number&userID=" + userID).trim();
	}
	
	public static JSONArray getMyBubbleList(String userID)
		throws SocketTimeoutException, JSONException {
		String getResult = Communicator.sendGet("?m=Square&a=showMyBubble&userID=" + userID);
		return new JSONArray(getResult);
	}

	/**
	 * Book the selected attraction
	 * 
	 * @param userID
	 * @param attrID
	 * @return
	 */
	public static String bookAttraction(String userID, String attrID, String starttime, String endtime)
		throws SocketTimeoutException {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd ");       
		String date = sDateFormat.format(new java.util.Date());
		return Communicator.sendPost("?m=Book&a=book",
				                     "&userID=" + userID +
				                     "&gameID=" + attrID +
				                     "&startTime=" + date + starttime +
				                     "&endTime=" + date + endtime);
	}

	// Methods for WaitingActivity
	/**
	 * Get the list of bubbles
	 * 
	 * @param userID
	 * @param playgroundID
	 * @return
	 */
	public static JSONArray getBubbleList(String playgroundID, String userID)
		throws SocketTimeoutException, JSONException {
		return new JSONArray(Communicator.sendGet("?m=Square&a=show&playgroundID=" + playgroundID + "&userID=" + userID));
	}

	/**
	 * post a Like request to the server
	 * 
	 * @param userID
	 * @param bubbleID
	 * @return
	 */
	public static String like(String userID, String bubbleID)
			throws SocketTimeoutException {
		return Communicator.sendPost("?m=Square&a=like", "&userID=" + userID + "&bubbleID=" + bubbleID);
	}
	
	public static String signIn(String userName, String password)
		throws SocketTimeoutException {
		return Communicator.sendPost("?m=Index&a=signIn", "&userName=" + userName +
				                                          "&password=" + password);
	}
	
	public static String signUp(String userName, String password, String userEmail)
			throws SocketTimeoutException {
			return Communicator.sendPost("?m=Index&a=signUp", "&userName=" + userName +
					                                          "&password=" + password +
					                                          "&userEmail=" + userEmail);
	}

	/**
	 * post an UnLike request to the server
	 * 
	 * @param userID
	 * @param bubbleID
	 * @return
	 */
	public static String unLike(String userID, String bubbleID) throws SocketTimeoutException {
		return Communicator.sendPost("?m=Square&a=unlike", "&userID=" + userID + "&bubbleID=" + bubbleID);
	}

	/**
	 * Get the list of comments of selected bubble
	 * 
	 * @param bubbleID
	 * @return
	 */
	public static JSONArray showComment(String bubbleID)
		throws SocketTimeoutException, JSONException {

		// TODO

		return null;
	}
	
	public static String postComment(String bubbleID, String userID,
			                         boolean isAnswer, String answerID,
			                         String content)
		throws SocketTimeoutException, JSONException {
		// TODO
		return null;
	}

	/**
	 * post a bubble to the server
	 * 
	 * @param <Image>
	 * @param userID
	 * @param playgroundID
	 * @param content
	 * @param image
	 * @return
	 */
	public static String postBubble(String userID, String playgroundID, String content)
		throws SocketTimeoutException {
		return Communicator.sendPost("?m=Square&a=bubblePost", "&userID=" + userID +
				                                               "&playgroundID=" + playgroundID +
				                                               "&bubbleContent=" + content);
	}
	
	public static String postBubbleComment(String bubbleID, String userID, String replyUserID, String commentContent)
		throws SocketTimeoutException {
		return Communicator.sendPost("?m=Square&a=postComment", "&userID=" + userID +
                												"&bubbleID=" + bubbleID +
                												"&replyUserID=" + replyUserID +
                												"&commentContent=" + commentContent);
	}

	// Method for WaitingActivity
	/**
	 * List all the waiting projects
	 * 
	 * @param userID
	 * @param playgroundID
	 * @return
	 */
	public static JSONArray showWaitingProject(String userID)
		throws SocketTimeoutException, JSONException {
		
		JSONArray getResult = new JSONArray(Communicator.sendGet("?m=Wait&a=show_1&userID=" + userID));
		
		return getResult;
	}

	// Method for RateActivity

	public static JSONArray listInrateAttraction(String userID)
		throws SocketTimeoutException, JSONException {
		JSONArray getResult = new JSONArray(Communicator.sendGet("?m=Wait&a=show_2&userID=" + userID));
		return getResult;
	}
	
	public static JSONArray planNow()
		throws SocketTimeoutException, JSONException {
		JSONArray getResult = new JSONArray(Communicator.sendGet("?m=Plan&a=plan"));
		return getResult;
	}
	
	public static JSONObject getBubbleCommentList(String bubbleID, String userID)
		throws SocketTimeoutException, JSONException {
		JSONObject getResult = new JSONObject(Communicator.sendGet("?m=Square&a=showComment&bubbleID=" + bubbleID
				                                                   + "&userID=" + userID));
		return getResult;
	}

	// Method for RatingActivity
	public static String rate(String userID, String attrID, int rate, String content)
		throws SocketTimeoutException {
		return Communicator.sendPost("?m=Wait&a=rate", "&userID=" + userID +
				                                       "&attractionID=" + attrID +
				                                       "&rateRank=" + rate +
				                                       "&content=" + content);
	}
}
