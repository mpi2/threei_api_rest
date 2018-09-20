import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';


import { AppComponent } from './app.component';
import { HomePageComponent } from './home-page/home-page.component';
import { DataPageComponent} from './data-page/data-page.component';
import { AnalysisPageComponent } from './analysis-page/analysis-page.component';
import { MethodsPageComponent } from './methods-page/methods-page.component';
import { ContactPageComponent } from './contact-page/contact-page.component';
import { FaqPageComponent } from './faq-page/faq-page.component';
import { GlossaryPageComponent } from './glossary-page/glossary-page.component';
import { PublicationsPageComponent } from './publications-page/publications-page.component';
import { PrivacyPageComponent } from './privacy-page/privacy-page.component';
import { LearnMorePageComponent } from './learn-more-page/learn-more-page.component';
import { ConsortiumPageComponent } from './consortium-page/consortium-page.component';
import { PhenotypeofthemonthPageComponent } from './phenotypeofthemonth-page/phenotypeofthemonth-page.component';
import {ImageacknowledgementsPageComponent} from "./imageacknowledgements-page/imageacknowledgements-page.component";
import { DetailsPageComponent } from './details-page/details-page.component';


const appRoutes: Routes = [
  {
    path: 'home',
    component: HomePageComponent,
  },
  {
    path: 'data',
    component: DataPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'analysis',
    component: AnalysisPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'methods',
    component: MethodsPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'contact',
    component: ContactPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'faq',
    component: FaqPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'glossary',
    component: GlossaryPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'publications',
    component: PublicationsPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'privacy',
    component: PrivacyPageComponent,
    // outlet: 'popup'
  },
  {
    path: 'consortium-meeting',
    component: ConsortiumPageComponent,
    //outlet: 'popup'
  },
  {
    path: 'phenotypeofthemonth',
    component: PhenotypeofthemonthPageComponent,
    //outlet: 'popup'
  },

  {
    path: 'image-acknowledgements',
    component: ImageacknowledgementsPageComponent,
    //outlet: 'popup'
  },

  {
    path: 'learn_more',
    component: LearnMorePageComponent,
    // outlet: 'popup'
  },

  {
    path: 'details',
    component: DetailsPageComponent,
    // outlet: 'popup'
  },

   { path: '',   redirectTo: 'home', pathMatch: 'full' }
//   { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(
      appRoutes,
      {
        enableTracing: false, // <-- debugging purposes only

      }
    )
  ],
  exports: [
    RouterModule
  ],
  providers: [

  ]
})
export class AppRoutingModule { }

