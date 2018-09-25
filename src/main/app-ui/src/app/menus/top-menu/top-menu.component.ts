import { Component, OnInit } from '@angular/core';
import { MatMenuModule } from '@angular/material';
import { Router } from '@angular/router';

@Component({
  selector: 'threei-top-menu',
  templateUrl: './top-menu.component.html',
  styleUrls: ['./top-menu.component.css']
})
export class TopMenuComponent implements OnInit {

  // constructor(private router: Router) { }
  constructor() { }

  ngOnInit() {
  }

  faqClick= function () {
    console.log('FAQ menu option selected');      
    // this.router.navigateByUrl('/user');
  };

  contactClick= function (){
    console.log('contact menu option selected');   
  }

}
