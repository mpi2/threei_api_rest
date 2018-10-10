import { Component, OnInit, Input } from '@angular/core';
import { MatMenuModule, MatToolbarModule,
  MatTooltipModule,} from '@angular/material';
import { Router } from '@angular/router';

@Component({
  selector: 'threei-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {

  @Input()
  topMenu: boolean;
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
