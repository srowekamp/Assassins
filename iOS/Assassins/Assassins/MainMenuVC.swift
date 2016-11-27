//
//  MainMenuVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 9/28/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class MainMenuVC: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    @IBAction func joinGame(_ sender: AnyObject) {
        performSegue(withIdentifier: "joinGame", sender: nil)
    }
    
    @IBAction func createGame(_ sender: AnyObject) {
        performSegue(withIdentifier: "createGame", sender: nil)
    }
    
    @IBAction func tapSettings(_ sender: AnyObject) {
        performSegue(withIdentifier: "settings", sender: nil)
    }

}
