//
//  ViewController.swift
//  Kill Testing
//
//  Created by Scott Rowekamp on 9/24/16.
//  Copyright © 2016 dev-scott. All rights reserved.
//

import UIKit
import AVFoundation

class ViewController: UIViewController {

    @IBOutlet weak var button: UIButton!
    
    var player = AVAudioPlayer()
    
    override func viewDidLoad() {
        super.viewDidLoad()
    
        let path = Bundle.main.path(forResource: "snipe", ofType: ".mp3")
        
        let url = URL(fileURLWithPath: path!)
        
        do {
            let sound = try AVAudioPlayer(contentsOf: url)
            player = sound
            player.prepareToPlay()
        } catch {
            // couldn't load file :(
        }
    
    
    }

    @IBAction func button_kill(_ sender: AnyObject) {
        player.stop()
        player.play()
    }
}

