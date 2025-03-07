const express = require('express');
const dotenv = require('dotenv');
const cookieParser= require('cookie-parser');
const connectDB = require('./config/db');
const mongoSanitize=require('express-mongo-sanitize');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');

const swaggerJsDoc = require('swagger-jsdoc');
const swaggerUI = require('swagger-ui-express');

const {xss} = require('express-xss-sanitizer');
const hpp = require('hpp');
const cors = require('cors');

//Load env vars
dotenv.config({path:'./config/config.env'});

//Connect to database
connectDB();
const dentists = require('./routes/dentists');
const auth = require('./routes/auth');
const appointments = require('./routes/appointments');
// const { default: rateLimit } = require('express-rate-limit');

//Route files
const app=express();

const swaggerOptions = {
    swaggerDefinition: {
      openapi: '3.0.0', // ระวังอย่าพิมพ์ openapi เป็น openai
      info: {
        title: 'Library API',
        version: '1.0.0',
        description: 'A simple Express VacQ API'
      },
    servers:[
        {
            url:'http://localhost:5000/api/v1'
        }
    ],
    },
    apis: ['./routes/*.js'],
};
  
const swaggerDocs = swaggerJsDoc(swaggerOptions);
app.use('/api-docs', swaggerUI.serve, swaggerUI.setup(swaggerDocs));

//Body parser
app.use(express.json());
app.use(cookieParser());

//Sanitize data
app.use(mongoSanitize());

//Set security header
app.use(helmet());

//Prevent XSS attacks
app.use(xss());

//Prevent http param pollutions
app.use(hpp());

//Enable CORS
app.use(cors());

//Rate Limiting
const limiter = rateLimit({
    windowMs: 10 * 60 * 1000, // 10 mins
    max: 5
  });

//app.use(limiter);
app.use('/api/v1/dentists', dentists);
app.use('/api/v1/auth', auth);
app.use('/api/v1/appointments', appointments);

const PORT=process.env.PORT || 5000;
const server = app.listen(PORT, console.log('Server running in ',process.env.NODE_ENV, ' mode on port ', PORT));

process.on( 'unhandleRejection',(err,promise)=>{
    console.log(`Error:' ${err.message}`);
    //Close server & exit process
    server.close(()=>process.exit(1));
});