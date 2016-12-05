//
//  TrackerVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/4/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import CoreLocation

class TrackerVC: UIViewController {

    @IBOutlet weak var compassImage: UIImageView!
    
    var updateTimer2:Timer?
    var currentRadians:CGFloat = 0.0
    
    @IBAction func goBack(_ sender: AnyObject) {
        self.dismiss(animated: true, completion: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateTimer2 = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(rotateImage), userInfo: nil, repeats: true)
    }
    
    func XXRadiansToDegrees(radians: Double) -> Double {
        return radians * 180.0 / M_PI
    }
    
    func getBearingBetweenTwoPoints(point1 : CLLocation, point2 : CLLocation) -> Double {
        // Returns a float with the angle between the two points
        let x = point1.coordinate.longitude - point2.coordinate.longitude
        let y = point1.coordinate.latitude - point2.coordinate.latitude
        
        return fmod(XXRadiansToDegrees(radians: atan2(y, x)), 360.0) + 90.0
    }
    
    func rotateImage() {
        if (currentRadians + CGFloat.pi / 4.0) >= 2 * CGFloat.pi {
            currentRadians = 0
        } else {
            currentRadians += CGFloat.pi / 4.0
        }
        compassImage.transform = CGAffineTransform(rotationAngle: currentRadians)
    }
}
