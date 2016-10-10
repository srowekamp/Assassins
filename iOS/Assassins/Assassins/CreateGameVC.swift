//
//  CreateGameVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit

class CreateGameVC: UIViewController {

    @IBOutlet weak var privateMatchSwitch: UISwitch!
    @IBOutlet weak var rangeSlider: UISlider!
    @IBOutlet weak var sliderValueLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        privateMatchSwitch.setOn(false, animated: true)
        sliderValueLabel.text = "\(rangeSlider.value)"
    }
    
    @IBAction func sliderChanged(_ sender: AnyObject) {
        sliderValueLabel.text = "\(rangeSlider.value)"
    }
    
    @IBAction func privateMatchSwitced(_ sender: AnyObject) {
        print("Switch Fliped")
    }
}
