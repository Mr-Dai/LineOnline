<?php

class TestAction extends Action{
	public function index(){
		
			$this->display();
		
	}
	
	public function insert() {
			$user['username'] = 'user1';
			$user['password'] = sha1('password1');
			$user['flag'] = 0;
			M('user')->add($user);
			echo $user['flag'];
	}
	
	public function show() {
			$user = M('user')->find();
			echo $user['username'];
			echo $user['password'];
			echo $user['flag'];
	}
	
	public function test() {
		$name = $_POST['username'];
		$pass = $_POST['password'];
		echo "name = ";
		echo $name;
		echo " pass= ";
		echo $pass;
	}
}
?>