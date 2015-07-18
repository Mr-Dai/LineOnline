<?php

class PlanAction extends Action{
	public function index(){
		
	}
	
	public function plan() {
			/*$paths = $_POST['path'];
			$num = count($panths);
			$arr = array();
			list($date,$time)=split(" ",date('Y-m-d H:i:s',time()));
			for($i = 0; $i < $num; $i++) {
				list($src,$dest,$dis) = split(",",$panths[$i]);
				list($key,$value) = split(":",$src);
				$tmp['src'] = intval($value);
				list($key,$value) = split(":",$dest);
				$tmp['dest'] = intval($value);
				list($key,$value) = split(":",$dis);
				$tmp['dis'] = float($value);
				array_push($arr,$tmp);
				
			}
			
			$result = array();
			for($i = 0; $i < $num; $i++) {
				$condition['gid']= $arr[$i]['src'];
				$firsttimes = M('gamevector')->where($condition)->select();
				$temp['first'] = $arr[$i]['src'];
				for($j = 0; $j < count($firsttimes); $j++) {
					$cond['flag'] = 1;
					$cond['gid'] = $arr[$i]['src'];
					$cond['starttime'] = $date.' '.$firsttimes['starttime']+':00';
					$cond['endttime'] = $date.' '.$firsttimes['endtime']+':00';
					$number = count(M('appointment')->where($cond)->select());
					if($number < $firsttimes['number']) {
						$temp['first_statttime'] = $cond['starttime'];
						$temp['first_endtime'] = $cond['endtime'];
						break;
					}
				}
				
				$temp['second'] = $arr[$i]['dest'];
				$condition['gid']= $arr[$i]['dest'];
				$secondtimes = M('gamevector')->where($condition)->select();
				for($j = 0; $j < count($secondtimes); $j++) {
					$cond['flag'] = 1;
					$cond['gid'] = $temp['dest'];
					$cond['starttime'] = $date.' '.$firsttimes['starttime']+':00';
					$cond['endttime'] = $date.' '.$firsttimes['endtime']+':00';
					$number = count(M('appointment')->where($cond)->select());
					if($number < $firsttimes['number'] && date('Y-m-d H:i:s',strtotime($temp['first_endtime'])+900) <= $cond['starttime']) {
						$temp['second_statttime'] = $cond['starttime'];
						$temp['second_endtime'] = $cond['endtime'];
						break;
					}
				}
				
				$third = 0; //找到最好的第三个点
				$mincost = 999999; //最小的代价
				for($j = 0; $j < $num; $j++) {
					if($arr[$j]['src'] == $arr[$i]['dest']) {
						$distance = $arr[$j]['dis'];
						$cost = $distance;
						if($mincost > $cost) {
							$third = $arr[$j]['src'];
							$mincost = $cost;
						}
					}
				}
				
				$temp['third'] = $third;
				$condition['gid']= $third;
				$secondtimes = M('gamevector')->where($condition)->select();
				for($j = 0; $j < count($secondtimes); $j++) {
					$cond['flag'] = 1;
					$cond['gid'] = $temp['dest'];
					$cond['starttime'] = $date.' '.$firsttimes['starttime']+':00';
					$cond['endttime'] = $date.' '.$firsttimes['endtime']+':00';
					$number = count(M('appointment')->where($cond)->select());
					if($number < $firsttimes['number'] && date('Y-m-d H:i:s',strtotime($temp['first_endtime'])+900) <= $cond['starttime']) {
						$temp['third_statttime'] = $cond['starttime'];
						$temp['third_endtime'] = $cond['endtime'];
						break;
					}
				}
				
				array_push($result,$temp);
				
			}
			
			//目的地和出发地对调的情况还没考虑
			
			$mincost = 999999999;
			$res = 0;
			for($i = 0; $i < $num; $i++)  {
				$cost = (strtotime($result[$i]['third_endtime'])-strtotime($result[$i]['src_starttime'])) + $result[$i]['dis']*1800;
				if($cost<$mincost) {
					$mincost = $cost;
					$res = $i;
				}
			}
			
			echo json_encode($result[$res]);*/
			
			$games = M('game')->select();
			$count = 0;
			$result = array();
			for($i = 0; $i<count($games);$i++) {
				if($count >= 3)
					break;
				$tmp = $games[$i];
				$condition['gid'] = $games[$i]['gid'];
				$tmp['timeterval'] = M('gamevector')->where($condition)->find();
				list($date,$time)=split(" ",date('Y-m-d H:i:s',time()));
				$cond = $condition;
				$cond['starttime'] = $date.' '.$tmp['timeterval']['starttime'].':00';
				$tmp['timeterval']['number'] -= count(M('appointment')->where($cond)->select());
				$count++;
				array_push($result,$tmp);
			}
			
			echo json_encode($result);
			
			
			
	}
	
	
}
?>