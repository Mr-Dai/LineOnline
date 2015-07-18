<?php

class WaitAction extends Action{
	public function index(){
		
		
	}
	
	public function show_1() {
		$condition['uid'] = $_GET['userID'];
		$appointments = M('appointment')->where($condition)->select();
		$len = count($appointments);
		$time = date('Y-m-d H:i:s',time());
		$result = array();
		for($i = 0; $i < $len; $i++) {
			if($time <= $appointments[$i]['endtime']){
				$cond['gid'] = $appointments[$i]['gid'];
				$game = M('game')->where($cond['gid'])->find();
				$tmp = $appointments[$i];
				$tmp['game'] = $game;
				array_push($result,$tmp);
			} else {
				$gameplayed['uid'] = $condition['uid'];
				$gameplayed['gid'] = $appointments[$i]['gid'];
				$gameplayed['flag'] = 1;
				M('gameplayed')->add($gameplayed);
			}
		}
		echo json_encode($result);
	}
	
	public function show_2() {
		$condition['uid'] = $_GET['userID'];
		$gameplayeds = M('gameplayed')->where($condition)->select();
		$len = count($gameplayeds);
		
		$result = array();
		for($i = 0; $i < $len; $i++) {
				$cond['gid'] = $gameplayeds[$i]['gid'];
				$game = M('game')->where($cond['gid'])->find();
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
		$gamecomment['flag'] = 1;
		$gamecomment['comment'] = "功能暂时没用";
		if($gamecomment['uid'] == "" ||$gamecomment['rank'] == "" ||$gamecomment['gid'] == "" ||){
			echo "false";
			return;
		}
		M('gamecomment')->add($gamecomment);
		echo "true";
		
	}
}
?>