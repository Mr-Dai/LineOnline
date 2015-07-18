<?php

class SquareAction extends Action{
	public function index(){
		
	}
	
	public function show() {
		$condition['pgid'] = $_GET['playgroundID'];
		$condition['flag'] = 1;
		$userID = $_GET['userID'];
		$bubbles = M('bubble')->where($condition)->order('createtime desc')->select();
		$result = array();
		$len = count($bubbles);
		for($i = 0; $i < $len ; $i++) {
			$cond['uid'] = $bubbles[$i]['uid'];
			$cond['flag'] = 1;
			$tmp = $bubbles[$i];
			$user = M('user')->where($cond)->find();
			$tmp['username'] = $user['username'];
			$tmp['isLike'] = "0";
			$cond['bid'] = $bubbles[$i]['bid'];
			$cond['uid'] = $userID;
			$c['bid'] = $bubbles[$i]['bid'];
			$c['flag'] = 1;
			$comments = M('comment')->where($c)->select();
			$tmp['commentNumber'] = count($comments);
			$like = M('good')->where($cond)->find();
			if($like && $like['flag'] == 1)
				$tmp['isLike'] = "1";
			array_push($result,$tmp);
		}
		
		echo json_encode($result);
	}
	
	public function like() {
		$condition['bid'] = $_POST['bubbleID'];
		
		$result = M('bubble')->where($condition)->find();
		$result['good'] = $result['good'] + 1;
		$condition['uid'] = $_POST['userID'];
		if($condition['bid']=="" || $condition['uid']==""){
			echo "bid is null or uid is null";
			return;
		}
		if(!$result) {
			echo "bid is not correct";
			return;
		}
		$good = M('good')->where($condition)->select();
		if(count($good) == 0) {
			$tmp['uid'] = $condition['uid'];
			$tmp['bid'] = $condition['bid'];
			$tmp['flag'] = 1;
			M('good')->add($tmp);
		}else {
			if($good[0]['flag'] == 0)
				$good[0]['flag'] = 1;
			else
				$result['good'] = $result['good']-1;
			M('good')->save($good[0]);
		}
		M('bubble')->save($result);
		echo "true";
	}
	
	public function unlike() {
		$condition['bid'] = $_POST['bubbleID'];
		$result = M('bubble')->where($condition)->find();
		$result['good'] = $result['good'] - 1;
		$condition['uid'] = $_POST['userID'];
		if($condition['bid']=="" || $condition['uid']==""){
			echo "bid is null or uid is null";
			return;
		}
		if(!$result) {
			echo "bid is not correct";
			return;
		}
		$good = M('good')->where($condition)->select();
		if(count($good) == 0) {
			$tmp['uid'] = $condition['uid'];
			$tmp['bid'] = $condition['bid'];
			M('good')->add($tmp);
		}else {
			$good[0]['flag'] = 0;
			M('good')->save($good[0]);
		}
		M('bubble')->save($result);
		echo "true";
	}
	
	public function showComment() {
		$condition['bid'] = $_GET['bubbleID'];
		$uid = $_GET['userID'];
		$condition['flag'] = 1;
		$comments = M('comment')->where($condition)->select();
		for($i = 0; $i < count($comments); $i++) {
			$cond['uid'] = $comments[$i]['uid'];
			$user = M('user')->where($cond)->find();
			$comments[$i]['username'] = $user['username'];
			$cond['uid'] = $comments[$i]['replayuid'];
			$user = M('user')->where($cond)->find();
			$comments[$i]['replyUsername'] = $user['username'];
		}
		$result = M('bubble')->where($condition)->find();
		$result['commentNumber'] = count($comments);
		$c['uid'] = $result['uid'];
		$user = M('user')->where($c)->find();
		$result['username'] = $user['username'];
		$result['islike'] = "0";
		$condition['uid'] = $uid;
		$like = M('good')->where($condition)->find();
		if($like && $like['flag'] == 1)
			$result['islike'] = "1";
		$result['comments'] = $comments;
		//echo json_encode($like);
		echo json_encode($result);
	}
	
	public function postComment() {
		$comment['bid'] = $_POST['bubbleID'];
		$comment['uid'] = $_POST['userID'];
		$comment['replayuid'] = $_POST['replyUserID'];
		$comment['content'] = $_POST['commentContent'];
		$comment['flag'] = 1;
		$comment['createtime'] = date('Y-m-d H:i:s',time());
		$comment['updatetime'] = date('Y-m-d H:i:s',time());
		if($comment['bid'] =="" || $comment['uid'] =="" || $comment['content'] =="" || $comment['replayuid'] =="") {
			echo "error!Something is empty";
			return;
		}
		M('comment')->add($comment);
		echo "true";
		echo json_encode($comment);
	}
	
	public function bubblePost() {
		$bubble['uid'] = $_POST['userID'];
		$bubble['pgid'] = $_POST['playgroundID'];
		$bubble['content'] = $_POST['bubbleContent'];
		$bubble['flag'] = 1;
		$bubble['createtime'] = date('Y-m-d H:i:s',time());
		$bubble['updatetime'] = date('Y-m-d H:i:s',time());
		if($bubble['uid']=="" || $bubble['pgid']==""||$bubble['content']=="") {
			echo "error! userID or content or playgroundID is empty";
			return;
		}
		M('bubble')->add($bubble);
		echo "true";
		echo json_encode($bubble);
	}
	
	public function showMyBubble() {
		$condition['uid'] = $_GET['userID'];
		$condition['flag'] = 1;
		$bubbles = M('bubble')->where($condition)->select();
		$len = count($bubbles);
		for($i = 0; $i < $len; $i++) {
			$cond['bid'] = $bubbles[$i]['bid'];
			$cond['flag'] = 1;
			$comments = M('comment')->where($cond)->select();
			$bubbles[$i]['commentNumber'] = count($comments);
		}
		echo json_encode($bubbles);
	}
	
}
?>