<?php

class WaitAction extends Action{
	public function index(){
		$arr = $_GET['arr'];
		echo json_encode($arr);
		echo "sfsafas";
	}
	
	public function show_1() {
		$condition['uid'] = $_GET['userID'];
		$condition['flag'] = 1;
		$appointments = M('appointment')->where($condition)->select();
		$len = count($appointments);
		$time = date('Y-m-d H:i:s',time());
		$result = array();
		for($i = 0; $i < $len; $i++) {
			if($time <= $appointments[$i]['endtime']){
				$cond['gid'] = $appointments[$i]['gid'];
				$game = M('game')->where($cond)->find();
				$tmp = $appointments[$i];
				$tmp['game'] = $game;
				array_push($result,$tmp);
			} else {
				$gameplayed['uid'] = $condition['uid'];
				$gameplayed['gid'] = $appointments[$i]['gid'];
				$gameplayed['flag'] = 1;
				M('gameplayed')->add($gameplayed);
				$appointments[$i]['flag'] = 0;
				M('appointment')->save($appointments[$i]);
			}
		}
		echo json_encode($result);
	}
	
	
	public function show_app_number() {
		$condition['uid'] = $_GET['userID'];
		$condition['flag'] = 1;
		$appointments = M('appointment')->where($condition)->select();
		$len = count($appointments);
		echo $len;
	}
	
	public function show_2() {
		$condition['uid'] = $_GET['userID'];
		$condition['flag'] = 1;
		$gameplayeds = M('gameplayed')->where($condition)->select();
		$len = count($gameplayeds);
		
		$result = array();
		for($i = 0; $i < $len; $i++) {
				$c['gid'] = $gameplayeds[$i]['gid'];
				$game = M('game')->where($c)->find();
				$tmp = $gameplayeds[$i];
				$tmp['game'] = $game;
				array_push($result,$tmp);
		}
		echo json_encode($result);
	}
	
	public function rate() {
		$gamecomment['uid'] = $_POST['userID'];
		$gamecomment['gid'] = $_POST['attractionID'];
		$gamecomment['rank'] = $_POST['rateRank'];
		$gamecomment['content'] = $_POST['content'];
		$gamecomment['flag'] = 1;
		if($gamecomment['uid'] == "" ||$gamecomment['rank'] == "" ||$gamecomment['gid'] == "" || $gamecomment['cooment']=""){
			echo "usrID rateRank attractionID content is empty";
			return;
		}
		$cond['uid'] = $gamecomment['uid'];
		$cond['gid'] = $gamecomment['gid']; 
		$gameplayed = M('gameplayed')->where($cond)->find();
		$gameplayed['flag'] = 0;
		M('gameplayed')->save($gameplayed);
		M('gamecomment')->add($gamecomment);
		$total = 0;
		$condition['gid'] = $gamecomment['gid'];
		$comments = M('gamecomment')->where($condition)->select();
		$len = count($comments);
		for($i = 0; $i < $len; $i++) {
			$total += $comments[$i]['rank'];
		}
		$total /= $len;
		$game = M('game')->where($condition)->find();
		$game['rank'] = $total;
		 M('game')->save($game);
		echo "true";
		
	}
}
?>